package com.ferelin.stockprice.ui.aboutSection.aboutPager

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class AboutPagerViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    ownerCompany: AdaptiveCompany?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private var mOwnerCompany: AdaptiveCompany? = ownerCompany

    private val mEventDataChanged = MutableSharedFlow<Unit>(1)
    val eventDataChanged: SharedFlow<Unit>
        get() = mEventDataChanged

    val companySymbol: String
        get() = mOwnerCompany?.companyProfile?.symbol ?: ""

    val companyName: String
        get() = mOwnerCompany?.companyProfile?.name ?: ""

    val companyFavouriteIconResource: Int
        get() = mOwnerCompany?.companyStyle?.favouriteSingleIconResource ?: 0

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mOwnerCompany?.let { onDataChanged(it) }

            mDataInteractor.companiesUpdatesShared
                .filter { filterSharedUpdate(it) }
                .collect { notificator ->
                    notificator.data?.let { onDataChanged(it) }
                }
        }
    }

    fun onFavouriteIconClicked() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mOwnerCompany?.let {
                if (it.isFavourite) {
                    mDataInteractor.removeCompanyFromFavourite(it)
                } else mDataInteractor.addCompanyToFavourite(it)
            }
        }
    }

    private suspend fun onDataChanged(company: AdaptiveCompany) {
        mOwnerCompany = company
        mEventDataChanged.emit(Unit)
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return notificator is DataNotificator.ItemUpdatedDefault &&
                mOwnerCompany?.companyProfile?.symbol == notificator.data?.companyProfile?.symbol
    }
}