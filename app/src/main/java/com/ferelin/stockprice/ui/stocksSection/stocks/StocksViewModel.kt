package com.ferelin.stockprice.ui.stocksSection.stocks

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksViewModel(
    contextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(contextProvider, dataInteractor) {

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

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
                    .collect { updateRecyclerItem(it) }
            }
            launch {
                mDataInteractor.openConnectionErrorState
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
            launch {
                mDataInteractor.favouriteCompaniesLimitReachedShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
            launch {
                mDataInteractor.loadCompanyQuoteErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.emit(it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Test", "onClear")
    }

    private fun onCompaniesPrepared(notificator: DataNotificator.DataPrepared<List<AdaptiveCompany>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val newList = ArrayList(notificator.data!!)
            withContext(mCoroutineContext.Main) {
                mRecyclerAdapter.setCompanies(newList)
            }
        }
    }
}