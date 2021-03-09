package com.ferelin.stockprice.ui.stocks

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.CoroutineContextProvider
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksViewModel(
    contextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(contextProvider, dataInteractor) {

    override fun initObservers() {
        super.initObservers()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState.collect {
                    withContext(mCoroutineContext.Main) {
                        onCompaniesStateUpdate(it)
                    }
                }
            }
            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    withContext(mCoroutineContext.Main) {
                        onFavouriteCompaniesStateUpdate(it)
                    }
                }
            }
        }

    }

    private fun onCompaniesStateUpdate(notificator: DataNotificator<List<AdaptiveCompany>>) {
        if (notificator is DataNotificator.Success) {
            mRecyclerAdapter.setCompanies(ArrayList(notificator.data))
        }
    }

    private fun onFavouriteCompaniesStateUpdate(notificator: DataNotificator<AdaptiveCompany>) {
        when (notificator) {
            is DataNotificator.NewItem -> {
                val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                if (index != -1) {
                    mRecyclerAdapter.updateCompany(notificator.data, index)
                }
            }
            is DataNotificator.Remove -> {
                val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                if (index != -1) {
                    mRecyclerAdapter.updateCompany(notificator.data, index)
                }
            }
            else -> Unit
        }
    }
}