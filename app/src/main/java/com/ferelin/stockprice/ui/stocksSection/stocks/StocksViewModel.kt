package com.ferelin.stockprice.ui.stocksSection.stocks

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

    private val mEventError = MutableSharedFlow<String>()
    val eventError: SharedFlow<String>
        get() = mEventError

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateCompanies() }
            launch { collectSharedOpenConnectionError() }
            launch { collectSharedFavouritesLimitReached() }
            launch { collectSharedCompanyQuoteError() }
        }
    }

    private suspend fun collectStateCompanies() {
        mDataInteractor.stateCompanies
            .filter { it is DataNotificator.DataPrepared }
            .take(1)
            .collect { onCompaniesPrepared(it as DataNotificator.DataPrepared<List<AdaptiveCompany>>) }
    }

    private suspend fun collectSharedOpenConnectionError() {
        mDataInteractor.sharedOpenConnectionError.collect { mEventError.emit(it) }
    }

    private suspend fun collectSharedFavouritesLimitReached() {
        mDataInteractor.sharedFavouriteCompaniesLimitReached.collect { mEventError.emit(it) }
    }

    private suspend fun collectSharedCompanyQuoteError() {
        mDataInteractor.sharedLoadCompanyQuoteError.collect { mEventError.emit(it) }
    }

    private fun onCompaniesPrepared(notificator: DataNotificator.DataPrepared<List<AdaptiveCompany>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val newList = ArrayList(notificator.data!!)
            withContext(mCoroutineContext.Main) {
                mStocksRecyclerAdapter.setCompanies(newList)
            }
        }
    }
}