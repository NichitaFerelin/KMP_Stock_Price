package com.ferelin.stockprice.ui.stocksSection.stocks

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class StocksViewModel(
    contextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(contextProvider, dataInteractor) {

    override fun initObserversBlock() {
        super.initObserversBlock()

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
                    .collect { updateNotifyRecyclerItem(it) }
            }
        }
    }

    private fun onCompaniesPrepared(notificator: DataNotificator.DataPrepared<List<AdaptiveCompany>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val newList = ArrayList(notificator.data!!)
            setRecyclerItems(newList)
        }
    }
}