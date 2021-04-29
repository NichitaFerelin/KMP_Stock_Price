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

    private var mIsDataSet = false

    private var mIsDataLoading = MutableStateFlow(true)
    val isDataLoading: StateFlow<Boolean>
        get() = mIsDataLoading

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

    private var mChartViewMode: ChartViewMode = ChartViewMode.All
    val chartViewMode: ChartViewMode
        get() = mChartViewMode

    private suspend fun onNetworkStateChanged(isAvailable: Boolean) {
        if (isAvailable && !mIsDataSet) {
            mIsDataLoading.value = true
            collectStockCandles()
        } else mIsDataLoading.value = false
    }

    private suspend fun collectStockCandles() {
        mDataInteractor.loadStockCandles(mSelectedCompany?.companyProfile?.symbol ?: "")
            .collect {
                onStockHistoryLoaded(
                    mDataInteractor.stockHistoryConverter.toCompanyHistoryForChart(
                        it.companyHistory
                    )
                )
            }
    }

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareData()

            launch {
                mDataInteractor.isNetworkAvailableState.collect {
                    onNetworkStateChanged(it)
                }
            }
            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { filterSharedUpdate(it) }
                    .collect { onDataChanged(it.data!!) }
            }
            launch {
                collectStockCandles()
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

    fun onChartControlButtonClicked(selectedViewMode: ChartViewMode) {
        mLastClickedMarker = null
        mChartViewMode = selectedViewMode
        mOriginalStockHistory?.let { convertHistoryToSelectedType(selectedViewMode) }
    }

    private fun convertHistoryToSelectedType(selectedViewMode: ChartViewMode) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val response = when (selectedViewMode) {
                is ChartViewMode.SixMonths -> {
                    mDataInteractor.stockHistoryConverter.toSixMonths(mOriginalStockHistory!!)
                }
                is ChartViewMode.Months -> {
                    mDataInteractor.stockHistoryConverter.toMonths(mOriginalStockHistory!!)
                }
                is ChartViewMode.Weeks -> {
                    mDataInteractor.stockHistoryConverter.toWeeks(mOriginalStockHistory!!)
                }
                is ChartViewMode.Year -> {
                    mDataInteractor.stockHistoryConverter.toOneYear(mOriginalStockHistory!!)
                }
                is ChartViewMode.All -> mOriginalStockHistory
                is ChartViewMode.Days -> mOriginalStockHistory
            }
            mEventStockHistoryChanged.emit(response!!)
        }
    }

    private fun prepareData() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSelectedCompany?.let {
                if (it.companyHistory.datePrices.isNotEmpty()) {
                    mIsDataSet = true
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
        mIsDataSet = true
        mIsDataLoading.value = false

        /*
        * Check if history from cache is equals to loaded history from network.
        * */
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