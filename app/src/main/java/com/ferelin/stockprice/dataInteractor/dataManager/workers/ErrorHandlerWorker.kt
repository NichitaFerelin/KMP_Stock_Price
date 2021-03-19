package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ErrorHandlerWorker(private val mContext: Context) {

    private val mPrepareCompaniesErrorState = MutableStateFlow("")
    val prepareCompaniesErrorState: StateFlow<String>
        get() = mPrepareCompaniesErrorState

    fun onPrepareCompaniesError() {
        mPrepareCompaniesErrorState.value = "prepare companies error"
    }

    private val mLoadStockCandlesErrorState = MutableSharedFlow<String>()
    val loadStockCandlesErrorState: SharedFlow<String>
        get() = mLoadStockCandlesErrorState

    suspend fun onLoadStockCandlesError() {
        mLoadStockCandlesErrorState.emit("load stock candles error")
    }

    private val mLoadCompanyNewsErrorState = MutableSharedFlow<String>()
    val loadCompanyNewsErrorState: SharedFlow<String>
        get() = mLoadCompanyNewsErrorState

    suspend fun onLoadCompanyNewsError() {
        mLoadCompanyNewsErrorState.emit("load company news error")
    }

    private val mOpenConnectionErrorState = MutableStateFlow("")
    val openConnectionErrorState: StateFlow<String>
        get() = mOpenConnectionErrorState

    fun onOpenConnectionError() {
        mOpenConnectionErrorState.value = "open connection error"
    }

    private val mLoadSearchRequestsErrorState = MutableSharedFlow<String>()
    val loadSearchRequestsErrorState: SharedFlow<String>
        get() = mLoadSearchRequestsErrorState

    suspend fun onLoadSearchRequestsError() {
        mLoadSearchRequestsErrorState.emit("load search error")
    }

    private val mFavouriteCompaniesLimitReachedState = MutableSharedFlow<String>()
    val favouriteCompaniesLimitReachedState: SharedFlow<String>
        get() = mFavouriteCompaniesLimitReachedState

    suspend fun onFavouriteCompaniesLimitReached() {
        mFavouriteCompaniesLimitReachedState.emit("limit favourite error")
    }
}