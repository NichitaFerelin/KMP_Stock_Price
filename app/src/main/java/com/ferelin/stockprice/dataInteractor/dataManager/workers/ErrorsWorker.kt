package com.ferelin.stockprice.dataInteractor.dataManager.workers

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.stockprice.R
import com.ferelin.stockprice.utils.getString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * [ErrorsWorker] providing different type-states of errors.
 */
class ErrorsWorker(private val mContext: Context) {

    private val mSharedApiLimitError = MutableSharedFlow<String>()
    val sharedApiLimitError: SharedFlow<String>
        get() = mSharedApiLimitError

    private val mSharedPrepareCompaniesError = MutableSharedFlow<String>()
    val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mSharedPrepareCompaniesError

    suspend fun onPrepareCompaniesError() {
        mSharedPrepareCompaniesError.emit(getString(mContext, R.string.errorPrepareData))
    }

    private val mSharedLoadCompanyQuoteError = MutableSharedFlow<String>()
    val sharedLoadCompanyQuoteError: SharedFlow<String>
        get() = mSharedLoadCompanyQuoteError

    suspend fun onLoadCompanyQuoteError(message: RepositoryMessages, companySymbol: String) {
        mSharedLoadCompanyQuoteError.emit(
            handleErrorWithLimit(message, R.string.errorLoadCompanyQuote, companySymbol)
        )
    }

    private val mSharedLoadStockCandlesError = MutableSharedFlow<String>()
    val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mSharedLoadStockCandlesError

    suspend fun onLoadStockCandlesError(message: RepositoryMessages, companySymbol: String) {
        mSharedLoadStockCandlesError.emit(
            handleErrorWithLimit(message, R.string.errorLoadStockCandles, companySymbol)
        )
    }

    private val mSharedLoadCompanyNewsError = MutableSharedFlow<String>()
    val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mSharedLoadCompanyNewsError

    suspend fun onLoadCompanyNewsError(message: RepositoryMessages, companySymbol: String) {
        mSharedLoadCompanyNewsError.emit(
            handleErrorWithLimit(message, R.string.errorLoadCompanyNews, companySymbol)
        )
    }

    private val mSharedOpenConnectionError = MutableSharedFlow<String>()
    val sharedOpenConnectionError: SharedFlow<String>
        get() = mSharedOpenConnectionError

    suspend fun onOpenConnectionError() {
        mSharedOpenConnectionError.emit(getString(mContext, R.string.errorLiveTimeDataLoad))
    }

    private val mSharedLoadSearchRequestsError = MutableSharedFlow<String>()
    val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mSharedLoadSearchRequestsError

    suspend fun onLoadSearchRequestsError() {
        mSharedLoadSearchRequestsError.emit(getString(mContext, R.string.errorLoadSearchesHistory))
    }

    private val mSharedFavouriteCompaniesLimitError = MutableSharedFlow<String>()
    val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mSharedFavouriteCompaniesLimitError

    suspend fun onFavouriteCompaniesLimitReached() {
        mSharedFavouriteCompaniesLimitError.emit(
            getString(mContext, R.string.errorFavouriteCompaniesLimit)
        )
    }

    private fun handleErrorWithLimit(
        message: RepositoryMessages,
        errorResource: Int,
        companySymbol: String
    ): String {
        return when (message) {
            RepositoryMessages.Limit -> getString(mContext, R.string.errorApiLimit)
            else -> String.format(
                getString(mContext, errorResource),
                companySymbol
            )
        }
    }
}