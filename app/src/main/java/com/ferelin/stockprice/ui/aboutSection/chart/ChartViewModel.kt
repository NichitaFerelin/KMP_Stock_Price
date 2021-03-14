package com.ferelin.stockprice.ui.aboutSection.chart

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ChartViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    arguments: Bundle?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mEventDataChanged = MutableSharedFlow<Unit>()
    val actionDataChanged: SharedFlow<Unit>
        get() = mEventDataChanged

    private var mCompanySymbol = arguments?.get(AboutPagerFragment.KEY_COMPANY_SYMBOL).toString()

    private var mCurrentPrice = arguments?.get(AboutPagerFragment.KEY_CURRENT_PRICE).toString()
    val currentPrice: String
        get() = mCurrentPrice

    private var mDayProfit = arguments?.get(AboutPagerFragment.KEY_DAY_PROFIT).toString()
    val dayProfit: String
        get() = mDayProfit

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { filterSharedUpdate(it) }
                    .collect { mEventDataChanged.emit(Unit) }
            }
        }
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return (notificator is DataNotificator.ItemUpdatedLiveTime
                || notificator is DataNotificator.ItemUpdatedQuote) &&
                notificator.data?.companyProfile?.symbol == mCompanySymbol &&
                notificator.data.companyDayData.currentPrice != mCurrentPrice
    }
}