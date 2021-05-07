package com.ferelin.stockprice.ui.aboutSection.aboutSection

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
    selectedCompany: AdaptiveCompany?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private var mSelectedCompany: AdaptiveCompany? = selectedCompany
    val selectedCompany: AdaptiveCompany?
        get() = mSelectedCompany

    private val mEventOnDataChanged = MutableSharedFlow<Unit>(1)
    val eventOnDataChanged: SharedFlow<Unit>
        get() = mEventOnDataChanged

    private var mSelectedTabPagePosition: Int = 0
    val selectedTabPagePosition: Int
        get() = mSelectedTabPagePosition

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedFavouriteCompaniesLimitReached

    val companySymbol: String
        get() = mSelectedCompany?.companyProfile?.symbol ?: ""

    val companyName: String
        get() = mSelectedCompany?.companyProfile?.name ?: ""

    val companyFavouriteIconResource: Int
        get() = mSelectedCompany?.companyStyle?.favouriteSingleIconResource ?: 0

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSelectedCompany?.let { onDataChanged(it) }
            collectSharedCompaniesUpdates()
        }
    }

    fun onFavouriteIconClicked() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSelectedCompany?.let {
                if (it.isFavourite) {
                    mDataInteractor.removeCompanyFromFavourite(it)
                } else mDataInteractor.addCompanyToFavourite(it)
            }
        }
    }

    fun onTabClicked(position: Int) {
        mSelectedTabPagePosition = position
    }

    private suspend fun collectSharedCompaniesUpdates() {
        mDataInteractor.sharedCompaniesUpdates
            .filter { filterUpdate(it) }
            .collect { onDataChanged(it.data!!) }
    }

    private suspend fun onDataChanged(company: AdaptiveCompany) {
        mSelectedCompany = company
        mEventOnDataChanged.emit(Unit)
    }

    private fun filterUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return notificator is DataNotificator.ItemUpdatedCommon
                && notificator.data != null
                && mSelectedCompany?.companyProfile?.symbol == notificator.data.companyProfile.symbol
    }
}