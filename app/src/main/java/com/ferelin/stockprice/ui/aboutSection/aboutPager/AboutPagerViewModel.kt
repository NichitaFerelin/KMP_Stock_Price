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
    selectedCompany: AdaptiveCompany?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private var mSelectedCompany: AdaptiveCompany? = selectedCompany
    val selectedCompany: AdaptiveCompany?
        get() = mSelectedCompany

    private val mEventDataChanged = MutableSharedFlow<Unit>(1)
    val eventDataChanged: SharedFlow<Unit>
        get() = mEventDataChanged

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

    private var mLastSelectedPage: Int = 0
    val lastSelectedPage: Int
        get() = mLastSelectedPage

    val companySymbol: String
        get() = mSelectedCompany?.companyProfile?.symbol ?: ""

    val companyName: String
        get() = mSelectedCompany?.companyProfile?.name ?: ""

    val companyFavouriteIconResource: Int
        get() = mSelectedCompany?.companyStyle?.favouriteSingleIconResource ?: 0

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mSelectedCompany?.let { onDataChanged(it) }

            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { filterSharedUpdate(it) }
                    .collect { onDataChanged(it.data!!) }
            }
            launch {
                mDataInteractor.favouriteCompaniesLimitReachedShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
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

    fun setSelectedTab(position: Int) {
        mLastSelectedPage = position
    }

    private suspend fun onDataChanged(company: AdaptiveCompany) {
        mSelectedCompany = company
        mEventDataChanged.emit(Unit)
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return notificator is DataNotificator.ItemUpdatedDefault &&
                notificator.data != null &&
                mSelectedCompany?.companyProfile?.symbol == notificator.data.companyProfile.symbol
    }
}