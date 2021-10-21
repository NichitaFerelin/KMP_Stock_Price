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

package com.ferelin.data_network_api.sources

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.data_network_api.entities.PastPricesApi
import com.ferelin.data_network_api.mappers.PastPriceMapper
import com.ferelin.data_network_api.utils.withExceptionHandle
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class PastPriceSourceImpl @Inject constructor(
    @Named("FinnhubToken") private val token: String,
    private val pastPricesApi: PastPricesApi,
    private val pastPriceMapper: PastPriceMapper,
    private val dispatchersProvider: DispatchersProvider
) : PastPriceSource {

    override suspend fun loadBy(
        companyId: Int,
        companyTicker: String,
        from: Long,
        to: Long,
        resolution: String
    ): LoadState<List<PastPrice>> = withContext(dispatchersProvider.IO) {
        Timber.d("get by (company ticker = $companyTicker)")

        withExceptionHandle(
            request = {
                pastPricesApi
                    .loadBy(companyTicker, token, from, to, resolution)
                    .execute()
            },
            onSuccess = { responseBody ->
                LoadState.Prepared(
                    data = pastPriceMapper.map(responseBody, companyId)
                )
            },
            onFail = { LoadState.Error() }
        )
    }
}