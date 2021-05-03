package com.ferelin.stockprice.ui.stocksSection.favourite

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    private val mEventOnNewItem = MutableSharedFlow<Unit>(1)
    val eventOnNewItem: SharedFlow<Unit>
        get() = mEventOnNewItem

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateFavouriteCompanies() }
            launch { collectSharedFavouriteCompaniesUpdates() }
        }
    }

    private suspend fun collectStateFavouriteCompanies() {
        mDataInteractor.stateFavouriteCompanies
            .filter { it is DataNotificator.DataPrepared }
            .take(1)
            .collect { onFavouriteCompaniesPrepared(it) }
    }

    private suspend fun collectSharedFavouriteCompaniesUpdates() {
        mDataInteractor.sharedFavouriteCompaniesUpdates
            .filter { it is DataNotificator.NewItemAdded || it is DataNotificator.ItemRemoved }
            .collect { onFavouriteCompanyUpdateShared(it) }
    }

    private fun onFavouriteCompaniesPrepared(notificator: DataNotificator<List<AdaptiveCompany>>) {
        mStocksRecyclerAdapter.setCompanies(ArrayList(notificator.data!!))
    }

    private fun onFavouriteCompanyUpdateShared(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            notificator.data?.let {
                when (notificator) {
                    is DataNotificator.NewItemAdded -> {
                        withContext(mCoroutineContext.Main) {
                            mStocksRecyclerAdapter.addCompany(notificator.data)
                            mEventOnNewItem.emit(Unit)
                        }
                    }
                    is DataNotificator.ItemRemoved -> {
                        val index = mStocksRecyclerAdapter.companies.indexOf(notificator.data)
                        if (index != NULL_INDEX) {
                            withContext(mCoroutineContext.Main) {
                                mStocksRecyclerAdapter.removeCompany(index)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}