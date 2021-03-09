package com.ferelin.stockprice.ui.favourite

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveLastPrice
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.CoroutineContextProvider
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    @FlowPreview
    override fun initObservers() {
        super.initObservers()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.favouriteCompaniesState.collect {
                    onFavouriteCompaniesStateChanged(it)
                }
            }
            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    onFavouriteCompaniesStateUpdate(it)
                }
            }
            launch {
                mDataInteractor.openConnection().collect {
                    onCompanyWebSocketResponse(it)
                }
            }
        }
    }

    private fun onFavouriteCompaniesStateChanged(notificator: DataNotificator<List<AdaptiveCompany>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (notificator is DataNotificator.Success) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.setCompanies(ArrayList(notificator.data))
                }
                notificator.data.forEach { mDataInteractor.subscribeItem(it.symbol) }
            }
        }
    }

    private fun onFavouriteCompaniesStateUpdate(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            when (notificator) {
                is DataNotificator.NewItem -> {
                    withContext(mCoroutineContext.Main) {
                        mRecyclerAdapter.addCompany(notificator.data)
                    }
                    mDataInteractor.subscribeItem(notificator.data.symbol)
                }
                // TODO try to unsubscribe
                is DataNotificator.Remove -> {
                    val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                    if (index != -1) {
                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.removeCompany(index)
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun onCompanyWebSocketResponse(lastData: AdaptiveLastPrice) {
        viewModelScope.launch(mCoroutineContext.IO) {
            lastData.company?.let {
                val index = mRecyclerAdapter.companies.indexOf(it)
                if (index != -1) {
                    withContext(mCoroutineContext.Main) {
                        mRecyclerAdapter.updateCompany(it, index)
                    }
                }
            }
        }
    }
}