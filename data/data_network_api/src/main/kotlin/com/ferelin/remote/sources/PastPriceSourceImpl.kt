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

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.remote.entities.PastPricesApi
import com.ferelin.remote.mappers.PastPriceMapper
import com.ferelin.remote.utils.withExceptionHandle
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class PastPriceSourceImpl @Inject constructor(
    private val mPastPricesApi: PastPricesApi,
    private val mPastPriceMapper: PastPriceMapper,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("FinnhubToken") private val mApiToken: String
) : PastPriceSource {

    override suspend fun loadPastPrices(
        companyId: Int,
        companyTicker: String,
        from: Long,
        to: Long,
        resolution: String
    ): LoadState<List<PastPrice>> = withContext(mDispatchersProvider.IO) {
        withExceptionHandle(
            request = {
                mPastPricesApi
                    .getPastPrices(companyTicker, mApiToken, from, to, resolution)
                    .execute()
            },
            onSuccess = { responseBody ->
                Timber.d("on success (responseBody = $responseBody)")
                LoadState.Prepared(
                    data = mPastPriceMapper.map(responseBody, companyId)
                )
            },
            onFail = {
                Timber.d("on exception (exception = $it)")
                LoadState.Error()
            }
        )
    }
}