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

    private val mActionScrollToTop = MutableSharedFlow<Unit>(1)
    val actionScrollToTop: SharedFlow<Unit>
        get() = mActionScrollToTop

    override fun initObserversBlock() {
        super.initObserversBlock()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.favouriteCompaniesState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect { onFavouriteCompaniesPrepared(it) }
            }.join()

            mDataInteractor.favouriteCompaniesUpdateShared
                .filter { it is DataNotificator.NewItemAdded || it is DataNotificator.ItemRemoved }
                .collect { onFavouriteCompanyUpdateShared(it) }
        }
    }

    private fun onFavouriteCompaniesPrepared(notificator: DataNotificator<List<AdaptiveCompany>>) {
        setRecyclerItems(notificator.data!!)
    }

    private fun onFavouriteCompanyUpdateShared(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            notificator.data?.let {
                when (notificator) {
                    is DataNotificator.NewItemAdded -> {
                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.addCompany(notificator.data)
                        }
                    }
                    is DataNotificator.ItemRemoved -> {
                        val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                        if (index != NULL_INDEX) {
                            withContext(mCoroutineContext.Main) {
                                mRecyclerAdapter.removeCompany(index)
                            }
                        }
                    }
                    else -> Unit
                }
                mActionScrollToTop.emit(Unit)
            }
        }
    }
}