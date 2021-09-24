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

package com.ferelin.remote.sources

import com.ferelin.domain.interactors.PastPriceState
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.remote.entities.PastPricesApi
import com.ferelin.remote.mappers.PastPriceMapper
import com.ferelin.remote.utils.withExceptionHandle
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class PastPriceSourceImpl @Inject constructor(
    private val mPastPricesApi: PastPricesApi,
    private val mPastPriceMapper: PastPriceMapper,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("FinnhubToken") private val mApiToken: String
) : PastPriceSource {

    override suspend fun loadPastPrices(
        companyTicker: String,
        from: Long,
        to: Long,
        resolution: String
    ): PastPriceState = withContext(mCoroutineContextProvider.IO) {
        withExceptionHandle(
            request = {
                mPastPricesApi
                    .getPastPrices(companyTicker, mApiToken, from, to, resolution)
                    .execute()
            },
            onSuccess = { responseBody ->
                PastPriceState.Loaded(mPastPriceMapper.map(responseBody))
            },
            onFail = { PastPriceState.Error }
        )
    }
}