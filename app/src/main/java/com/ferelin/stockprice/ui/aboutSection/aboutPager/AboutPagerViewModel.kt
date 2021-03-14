package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class AboutPagerViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    arguments: Bundle?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mEventIconChanged = MutableSharedFlow<Unit>()
    val eventIconChanged: MutableSharedFlow<Unit>
        get() = mEventIconChanged

    private var mCompanyName: String =
        arguments?.get(AboutPagerFragment.KEY_COMPANY_NAME).toString()
    val companyName: String
        get() = mCompanyName

    private var mCompanySymbol: String =
        arguments?.get(AboutPagerFragment.KEY_COMPANY_SYMBOL).toString()
    val companySymbol: String
        get() = mCompanySymbol

    private var mCompanyFavouriteIconResource: Int =
        arguments?.get(AboutPagerFragment.KEY_FAVOURITE_ICON_RESOURCE) as Int
    val companyFavouriteIconResource: Int
        get() = mCompanyFavouriteIconResource

    fun onFavouriteIconClicked() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.removeCompanyFromFavourite(mCompanySymbol)
        }
    }

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.companiesUpdatesShared
                .filter { filterSharedUpdate(it) }
                .collect { mEventIconChanged.emit(Unit) }
        }
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return notificator is DataNotificator.ItemUpdatedDefault &&
                mCompanySymbol == notificator.data?.companyProfile?.symbol &&
                mCompanyFavouriteIconResource != notificator.data.companyStyle.favouriteSingleIconResource

    }
}