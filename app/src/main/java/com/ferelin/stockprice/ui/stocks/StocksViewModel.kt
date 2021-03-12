package com.ferelin.stockprice.ui.stocks

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class StocksViewModel(
    contextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(contextProvider, dataInteractor) {

    init {
        initObservers()
    }

    override fun initObservers() {
        super.initObservers()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect { onCompaniesPrepared(it as DataNotificator.DataPrepared<List<AdaptiveCompany>>) }
            }

            launch {
                mDataInteractor.companiesUpdatesShared
                    .filter { it is DataNotificator.ItemUpdatedDefault }
                    .collect { updateRecyclerItem(it) }
            }
        }
    }

    private fun onCompaniesPrepared(notificator: DataNotificator.DataPrepared<List<AdaptiveCompany>>) {
        viewModelScope.launch(mCoroutineContext.Main) {
            mRecyclerAdapter.setCompanies(ArrayList(notificator.data))
        }
    }
}