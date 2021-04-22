package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.content.Context
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.stockprice.R
import com.ferelin.stockprice.utils.getString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/*
* Worker that is responsible for:
*   - Notification when error has occurred
* */
class ErrorHandlerWorker(private val mContext: Context) {

    private val mApiLimitError = MutableStateFlow(false)
    val apiLimitError: StateFlow<Boolean>
        get() = mApiLimitError

    fun onSuccessResponse() {
        mApiLimitError.value = false
    }

    private val mPrepareCompaniesErrorShared = MutableSharedFlow<String>()
    val prepareCompaniesErrorShared: SharedFlow<String>
        get() = mPrepareCompaniesErrorShared

    suspend fun onPrepareCompaniesErrorGot() {
        mPrepareCompaniesErrorShared.emit(getString(mContext, R.string.errorPrepareData))
    }

    private val mLoadCompanyQuoteErrorShared = MutableSharedFlow<String>()
    val loadCompanyQuoteErrorShared: SharedFlow<String>
        get() = mLoadCompanyQuoteErrorShared

    suspend fun onLoadCompanyQuoteErrorGot(message: RepositoryMessages, companySymbol: String) {
        mLoadCompanyQuoteErrorShared.emit(
            handleErrorWithLimit(message, R.string.errorLoadCompanyQuote, companySymbol)
        )
    }

    private val mLoadStockCandlesErrorShared = MutableSharedFlow<String>()
    val loadStockCandlesErrorShared: SharedFlow<String>
        get() = mLoadStockCandlesErrorShared

    suspend fun onLoadStockCandlesErrorGot(message: RepositoryMessages, companySymbol: String) {
        mLoadStockCandlesErrorShared.emit(
            handleErrorWithLimit(message, R.string.errorLoadStockCandles, companySymbol)
        )
    }

    private val mLoadCompanyNewsErrorShared = MutableSharedFlow<String>()
    val loadCompanyNewsErrorShared: SharedFlow<String>
        get() = mLoadCompanyNewsErrorShared

    suspend fun onLoadCompanyNewsErrorGot(message: RepositoryMessages, companySymbol: String) {
        mLoadCompanyNewsErrorShared.emit(
            handleErrorWithLimit(message, R.string.errorLoadCompanyNews, companySymbol)
        )
    }

    private val mOpenConnectionErrorState = MutableStateFlow("")
    val openConnectionErrorState: StateFlow<String>
        get() = mOpenConnectionErrorState

    fun onOpenConnectionErrorGot() {
        mOpenConnectionErrorState.value = getString(mContext, R.string.errorLiveTimeDataLoad)
    }

    private val mLoadSearchRequestsErrorShared = MutableSharedFlow<String>()
    val loadSearchRequestsErrorShared: SharedFlow<String>
        get() = mLoadSearchRequestsErrorShared

    suspend fun onLoadSearchRequestsErrorGot() {
        mLoadSearchRequestsErrorShared.emit(getString(mContext, R.string.errorLoadSearchesHistory))
    }

    private val mFavouriteCompaniesLimitErrorShared = MutableSharedFlow<String>()
    val favouriteCompaniesLimitReachedState: SharedFlow<String>
        get() = mFavouriteCompaniesLimitErrorShared

    suspend fun onFavouriteCompaniesLimitReached() {
        mFavouriteCompaniesLimitErrorShared.emit(
            getString(mContext, R.string.errorFavouriteCompaniesLimit)
        )
    }

    private fun handleErrorWithLimit(
        message: RepositoryMessages,
        errorResource: Int,
        companySymbol: String
    ): String {
        return when (message) {
            RepositoryMessages.Limit -> {
                mApiLimitError.value = true
                ""
            }
            else -> String.format(
                getString(mContext, errorResource),
                companySymbol
            )
        }
    }
}