package com.ferelin.stockprice.ui.aboutSection.chart

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseDataViewModel
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChartViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    selectedCompany: AdaptiveCompany?
) : BaseDataViewModel(coroutineContextProvider, dataInteractor) {

    private val mSelectedCompany: AdaptiveCompany? = selectedCompany
    private var mOriginalStockHistory: AdaptiveCompanyHistoryForChart? = null

    private var mLastClickedMarker: Marker? = null
    val lastClickedMarker: Marker?
        get() = mLastClickedMarker

    private val mHasDataForChart =
        MutableStateFlow(mSelectedCompany?.companyHistory?.closePrices?.isNotEmpty() ?: false)
    val hasDataForChartState: StateFlow<Boolean>
        get() = mHasDataForChart

    private val mEventDayDataChanged = MutableSharedFlow<Unit>(1)
    val eventDataChanged: SharedFlow<Unit>
        get() = mEventDayDataChanged

    private val mEventStockHistoryChanged = MutableSharedFlow<AdaptiveCompanyHistoryForChart>(1)
    val eventStockHistoryChanged: SharedFlow<AdaptiveCompanyHistoryForChart>
        get() = mEventStockHistoryChanged

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

    private var mCurrentPrice = selectedCompany?.companyDayData?.currentPrice ?: ""
    val currentPrice: String
        get() = mCurrentPrice

    private var mDayProfit = selectedCompany?.companyDayData?.profit ?: ""
    val dayProfit: String
        get() = mDayProfit

    private var mProfitBackground = selectedCompany?.companyStyle?.dayProfitBackground ?: 0
    val profitBackground: Int
        get() = mProfitBackground

    private var mChartSelectedViewMode: ChartSelectedViewMode = ChartSelectedViewMode.All
    val chartSelectedViewMode: ChartSelectedViewMode
        get() = mChartSelectedViewMode

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareData()

            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { filterSharedUpdate(it) }
                    .collect { onDataChanged(it.data!!) }
            }
            launch {
                mDataInteractor.loadStockCandles(mSelectedCompany?.companyProfile?.symbol ?: "")
                    .collect {
                        onStockHistoryLoaded(
                            mDataInteractor.stockHistoryConverter.toCompanyHistoryForChart(
                                it.companyHistory
                            )
                        )
                    }
            }
            launch {
                mDataInteractor.loadStockCandlesErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
        }
    }

    fun onChartClicked(marker: Marker): Boolean {
        return if (mLastClickedMarker != marker) {
            mLastClickedMarker = marker
            true
        } else false
    }

    fun onChartControlButtonClicked(selectedViewMode: ChartSelectedViewMode) {
        mLastClickedMarker = null
        mChartSelectedViewMode = selectedViewMode
        mOriginalStockHistory?.let { convertHistoryToSelectedType(selectedViewMode) }
    }

    private fun convertHistoryToSelectedType(selectedViewMode: ChartSelectedViewMode) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val response = when (selectedViewMode) {
                is ChartSelectedViewMode.SixMonths -> {
                    mDataInteractor.stockHistoryConverter.toSixMonths(mOriginalStockHistory!!)
                }
                is ChartSelectedViewMode.Months -> {
                    mDataInteractor.stockHistoryConverter.toMonths(mOriginalStockHistory!!)
                }
                is ChartSelectedViewMode.Weeks -> {
                    mDataInteractor.stockHistoryConverter.toWeeks(mOriginalStockHistory!!)
                }
                is ChartSelectedViewMode.Year -> {
                    mDataInteractor.stockHistoryConverter.toOneYear(mOriginalStockHistory!!)
                }
                is ChartSelectedViewMode.All -> mOriginalStockHistory
                is ChartSelectedViewMode.Days -> mOriginalStockHistory
            }
            mEventStockHistoryChanged.emit(response!!)
        }
    }

    private fun prepareData() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSelectedCompany?.let {
                if (it.companyHistory.datePrices.isNotEmpty()) {
                    mDataInteractor.stockHistoryConverter.toCompanyHistoryForChart(it.companyHistory)
                        .also { historyForChart ->
                            mOriginalStockHistory = historyForChart
                            mEventStockHistoryChanged.emit(historyForChart)
                        }
                }
                onDataChanged(it)
            }
        }
    }

    private suspend fun onDataChanged(company: AdaptiveCompany) {
        company.apply {
            mCurrentPrice = companyDayData.currentPrice
            mDayProfit = companyDayData.profit
            mProfitBackground = companyStyle.dayProfitBackground
            mEventDayDataChanged.emit(Unit)
        }
    }

    private suspend fun onStockHistoryLoaded(history: AdaptiveCompanyHistoryForChart) {
        if (history.dates.first() != mOriginalStockHistory?.dates?.firstOrNull()) {
            mOriginalStockHistory = history
            mHasDataForChart.value = true
            mEventStockHistoryChanged.emit(history)
        }
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return (notificator is DataNotificator.ItemUpdatedLiveTime
                || notificator is DataNotificator.ItemUpdatedQuote)
                && notificator.data != null
                && notificator.data.companyProfile.symbol == mSelectedCompany?.companyProfile?.symbol
    }
}