package com.ferelin.stockprice.ui.aboutSection.chart

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ChartViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    company: AdaptiveCompany?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mOwnerCompany: AdaptiveCompany? = company

    private var mOriginalStockHistory: AdaptiveCompanyHistoryForChart? = null

    private var mLastClickedMarker: Marker? = null
    val lastClickedMarker: Marker?
        get() = mLastClickedMarker

    private var mChartSelectedType: ChartSelectedType = ChartSelectedType.All
    val chartSelectedType: ChartSelectedType
        get() = mChartSelectedType

    private val mEventStockHistoryChanged = MutableSharedFlow<AdaptiveCompanyHistoryForChart>(1)
    val eventStockHistoryChanged: SharedFlow<AdaptiveCompanyHistoryForChart>
        get() = mEventStockHistoryChanged

    private val mEventDataChanged = MutableSharedFlow<Unit>(1)
    val eventDataChanged: SharedFlow<Unit>
        get() = mEventDataChanged

    private var mCurrentPrice = company?.companyDayData?.currentPrice ?: ""
    val currentPrice: String
        get() = mCurrentPrice

    private var mDayProfit = company?.companyDayData?.profit ?: ""
    val dayProfit: String
        get() = mDayProfit

    private var mProfitBackground = company?.companyStyle?.dayProfitBackground ?: 0
    val profitBackground: Int
        get() = mProfitBackground

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mOwnerCompany?.let {
                    if (it.companyHistory.datePrices.isNotEmpty()) {
                        mDataInteractor.stockHistoryConverter.toCompanyHistoryForChart(it.companyHistory)
                            .also { historyForChart ->
                                mOriginalStockHistory = historyForChart
                                mEventStockHistoryChanged.emit(historyForChart)
                            }
                    }
                    onDataChanged(it)
                }
            }.join()

            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { filterSharedUpdate(it) }
                    .collect { notificator ->
                        notificator.data?.let { onDataChanged(it) }
                    }
            }


            launch {
                mDataInteractor.loadStockCandles(mOwnerCompany?.companyProfile?.symbol ?: "")
                    .collect {
                        onStockHistoryLoaded(
                            mDataInteractor.stockHistoryConverter.toCompanyHistoryForChart(
                                it.companyHistory
                            )
                        )
                    }
            }
        }
    }

    fun onChartClicked(marker: Marker): Boolean {
        return if (mLastClickedMarker != marker) {
            mLastClickedMarker = marker
            true
        } else false
    }

    fun onChartControlButtonClicked(selectedType: ChartSelectedType) {
        mChartSelectedType = selectedType
        mOriginalStockHistory?.let { convertHistoryToSelectedType(selectedType) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertHistoryToSelectedType(selectedType: ChartSelectedType) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val response = when (selectedType) {
                is ChartSelectedType.SixMonths -> {
                    mDataInteractor.stockHistoryConverter.toSixMonths(mOriginalStockHistory!!)
                }
                is ChartSelectedType.Months -> {
                    mDataInteractor.stockHistoryConverter.toMonths(mOriginalStockHistory!!)
                }
                is ChartSelectedType.Weeks -> {
                    mDataInteractor.stockHistoryConverter.toWeeks(mOriginalStockHistory!!)
                }
                is ChartSelectedType.Year -> {
                    mDataInteractor.stockHistoryConverter.toOneYear(mOriginalStockHistory!!)
                }
                is ChartSelectedType.All -> mOriginalStockHistory
                is ChartSelectedType.Days -> mOriginalStockHistory
            }
            mEventStockHistoryChanged.emit(response!!)
        }
    }

    private suspend fun onDataChanged(company: AdaptiveCompany) {
        company.apply {
            mCurrentPrice = companyDayData.currentPrice
            mDayProfit = companyDayData.profit
            mProfitBackground = companyStyle.dayProfitBackground
            mEventDataChanged.emit(Unit)
        }
    }

    private suspend fun onStockHistoryLoaded(history: AdaptiveCompanyHistoryForChart) {
        if (history.dates.first() != mOriginalStockHistory?.dates?.firstOrNull()) {
            mOriginalStockHistory = history
            mEventStockHistoryChanged.emit(history)
        }
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return (notificator is DataNotificator.ItemUpdatedLiveTime
                || notificator is DataNotificator.ItemUpdatedQuote)
                && notificator.data?.companyProfile?.symbol == mOwnerCompany?.companyProfile?.symbol
    }
}