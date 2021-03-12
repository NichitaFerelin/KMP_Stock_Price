package com.ferelin.stockprice.ui.favourite

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    init {
        initObservers()
    }

    override fun initObservers() {
        super.initObservers()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.favouriteCompaniesState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect { onFavouriteCompaniesPrepared(it) }
            }

            launch {
                mDataInteractor.favouriteCompaniesUpdateShared
                    .filter { it is DataNotificator.NewItemAdded || it is DataNotificator.ItemRemoved }
                    .collect { onFavouriteCompanyUpdateShared(it) }
            }
        }
    }

    private fun onFavouriteCompaniesPrepared(notificator: DataNotificator<List<AdaptiveCompany>>) {
        mRecyclerAdapter.setCompanies(ArrayList(notificator.data!!))
    }

    private fun onFavouriteCompanyUpdateShared(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (notificator is DataNotificator.NewItemAdded) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addCompany(notificator.data!!)
                }
            } else if (notificator is DataNotificator.ItemRemoved) {
                val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                if (index != NULL_INDEX) {
                    withContext(mCoroutineContext.Main) {
                        mRecyclerAdapter.removeCompany(index)
                    }
                }
            }
        }
    }
}