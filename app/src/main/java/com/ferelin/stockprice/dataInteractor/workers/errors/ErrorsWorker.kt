package com.ferelin.stockprice.dataInteractor.workers.errors

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ErrorsWorker] provides different side-states of errors.
 */
@Singleton
open class ErrorsWorker @Inject constructor(
    private val mContext: Context,
) : ErrorsWorkerStates {

    private val mSharedApiLimitError = MutableSharedFlow<String>()
    override val sharedApiLimitError: SharedFlow<String>
        get() = mSharedApiLimitError

    private val mSharedPrepareCompaniesError = MutableSharedFlow<String>()
    override val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mSharedPrepareCompaniesError

    suspend fun onPrepareCompaniesError() {
        mSharedPrepareCompaniesError.emit(mContext.getString(R.string.errorPrepareData))
    }

    private val mSharedLoadStockCandlesError = MutableSharedFlow<String>()
    override val sharedLoadStockHistoryError: SharedFlow<String>
        get() = mSharedLoadStockCandlesError

    suspend fun onLoadStockHistoryError(
        message: RepositoryMessages,
        companySymbol: String
    ) {
        mSharedLoadStockCandlesError.emit(
            handleErrorWithLimit(message, R.string.errorLoadStockCandles, companySymbol)
        )
    }

    private val mSharedLoadCompanyNewsError = MutableSharedFlow<String>()
    override val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mSharedLoadCompanyNewsError

    suspend fun onLoadCompanyNewsError(
        message: RepositoryMessages,
        companySymbol: String
    ) {
        mSharedLoadCompanyNewsError.emit(
            handleErrorWithLimit(message, R.string.errorLoadCompanyNews, companySymbol)
        )
    }

    private val mSharedLoadSearchRequestsError = MutableSharedFlow<String>()
    override val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mSharedLoadSearchRequestsError

    suspend fun onLoadSearchRequestsError() {
        mSharedLoadSearchRequestsError.emit(mContext.getString(R.string.errorLoadSearchesHistory))
    }

    private val mSharedFavouriteCompaniesLimitError = MutableSharedFlow<String>()
    override val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mSharedFavouriteCompaniesLimitError

    suspend fun onFavouriteCompaniesLimitReached() {
        mSharedFavouriteCompaniesLimitError.emit(
            mContext.getString(R.string.errorFavouriteCompaniesLimit)
        )
    }

    private val mSharedAuthenticationError = MutableSharedFlow<String>()
    override val sharedAuthenticationError: SharedFlow<String>
        get() = mSharedAuthenticationError

    suspend fun onAuthenticationError(message: RepositoryMessages) {
        val errorMessage = when (message) {
            RepositoryMessages.Limit -> mContext.getString(R.string.errorTooManyRequests)
            else -> mContext.getString(R.string.errorSmthWentWrong)
        }
        mSharedAuthenticationError.emit(errorMessage)
    }

    /**
     * Errors can be caused by the API limit, in which case the alternative error message
     * is meaningless.
     * */
    private fun handleErrorWithLimit(
        message: RepositoryMessages,
        errorResource: Int,
        companySymbol: String
    ): String {
        return when (message) {
            RepositoryMessages.Limit -> mContext.getString(R.string.errorApiLimit)
            else -> String.format(
                mContext.getString(errorResource),
                companySymbol
            )
        }
    }
}