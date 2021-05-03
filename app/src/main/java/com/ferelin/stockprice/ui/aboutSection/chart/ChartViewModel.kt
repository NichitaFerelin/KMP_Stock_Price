package com.ferelin.stockprice.ui.aboutSection.chart

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChartViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    selectedCompany: AdaptiveCompany?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mSelectedCompany: AdaptiveCompany? = selectedCompany

    val isHistoryEmpty: Boolean
        get() = mSelectedCompany?.companyHistory?.datePrices?.isEmpty() ?: true

    /*
    * There are different modes of displaying data.
    * For each of them, need to convert the data. This is the original list that can be used for this.
    */
    private var mOriginalStockHistory: AdaptiveCompanyHistoryForChart? = null

    private val mStateIsNetworkResponded = MutableStateFlow(false)

    private val mStateStockHistory = MutableStateFlow<AdaptiveCompanyHistoryForChart?>(null)
    val stateStockHistory: StateFlow<AdaptiveCompanyHistoryForChart?>
        get() = mStateStockHistory

    private val mStateIsDataLoading = MutableStateFlow(false)
    val stateIsDataLoading: StateFlow<Boolean>
        get() = mStateIsDataLoading

    private val mEventOnDayDataChanged = MutableSharedFlow<Unit>(1)
    val eventOnDayDataChanged: SharedFlow<Unit>
        get() = mEventOnDayDataChanged

    private val mEventOnError = MutableSharedFlow<String>()
    val eventOnError: SharedFlow<String>
        get() = mEventOnError

    private var mChartViewMode: ChartViewMode = ChartViewMode.All
    val chartViewMode: ChartViewMode
        get() = mChartViewMode

    private var mClickedMarker: Marker? = null
    val clickedMarker: Marker?
        get() = mClickedMarker

    val stockPrice: String
        get() = mSelectedCompany?.companyDayData?.currentPrice ?: ""

    val dayProfit: String
        get() = mSelectedCompany?.companyDayData?.profit ?: ""

    val profitBackgroundResource: Int
        get() = mSelectedCompany?.companyStyle?.dayProfitBackground ?: 0

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareData()
            launch { collectStateNetworkAvailable() }
            launch { collectSharedCompaniesUpdates() }
            launch { collectSharedError() }
        }
    }

    fun onChartClicked(marker: Marker) {
        mClickedMarker = marker
    }

    fun onChartControlButtonClicked(selectedViewMode: ChartViewMode) {
        mClickedMarker = null
        mChartViewMode = selectedViewMode
        mOriginalStockHistory?.let { convertHistoryToSelectedType(selectedViewMode) }
    }

    private suspend fun collectStockCandles() {
        val selectedCompanySymbol = mSelectedCompany!!.companyProfile.symbol
        mDataInteractor.loadStockCandles(selectedCompanySymbol).collect { responseCompany ->
            onStockHistoryLoaded(responseCompany)
        }
    }

    private suspend fun collectStateNetworkAvailable() {
        mDataInteractor.stateIsNetworkAvailable.collect { onNetworkStateChanged(it) }
    }

    private suspend fun collectSharedCompaniesUpdates() {
        mDataInteractor.sharedCompaniesUpdates
            .filter { filterSharedUpdate(it) }
            .collect { mEventOnDayDataChanged.emit(Unit) }
    }

    private suspend fun collectSharedError() {
        mDataInteractor.sharedLoadStockCandlesError.collect { mEventOnError.emit(it) }
    }

    private fun prepareData() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareChart()

            // Trigger view observer to take data from view model
            mEventOnDayDataChanged.emit(Unit)
        }
    }

    private fun prepareChart() {
        if (isHistoryEmpty) {
            return
        }

        val adaptiveHistory = mDataInteractor.stockHistoryConverter
            .toCompanyHistoryForChart(mSelectedCompany!!.companyHistory)
        mOriginalStockHistory = adaptiveHistory
        mStateStockHistory.value = adaptiveHistory
    }

    private suspend fun onNetworkStateChanged(isAvailable: Boolean) {
        if (isAvailable && !mStateIsNetworkResponded.value) {
            mStateIsDataLoading.value = true
            collectStockCandles()
        } else mStateIsDataLoading.value = false
    }

    private fun onStockHistoryLoaded(company: AdaptiveCompany) {
        val adaptiveHistory = mDataInteractor.stockHistoryConverter
            .toCompanyHistoryForChart(company.companyHistory)
        mStateIsNetworkResponded.value = true
        mStateIsDataLoading.value = false

        if (isNewData(adaptiveHistory)) {
            mOriginalStockHistory = adaptiveHistory
            mStateStockHistory.value = adaptiveHistory
        }
    }

    private fun convertHistoryToSelectedType(selectedViewMode: ChartViewMode) {
        viewModelScope.launch(mCoroutineContext.IO) {
            with(mDataInteractor.stockHistoryConverter) {
                val convertedHistory = when (selectedViewMode) {
                    is ChartViewMode.SixMonths -> toSixMonths(mOriginalStockHistory!!)
                    is ChartViewMode.Months -> toMonths(mOriginalStockHistory!!)
                    is ChartViewMode.Weeks -> toWeeks(mOriginalStockHistory!!)
                    is ChartViewMode.Year -> toOneYear(mOriginalStockHistory!!)
                    is ChartViewMode.All -> mOriginalStockHistory
                    is ChartViewMode.Days -> mOriginalStockHistory
                }
                mStateStockHistory.value = convertedHistory!!
            }
        }
    }

    private fun isNewData(loadedHistory: AdaptiveCompanyHistoryForChart): Boolean {
        return loadedHistory != mOriginalStockHistory
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return (notificator is DataNotificator.ItemUpdatedLiveTime
                || notificator is DataNotificator.ItemUpdatedQuote)
                && notificator.data != null
                && notificator.data.companyProfile.symbol == mSelectedCompany?.companyProfile?.symbol
    }
}