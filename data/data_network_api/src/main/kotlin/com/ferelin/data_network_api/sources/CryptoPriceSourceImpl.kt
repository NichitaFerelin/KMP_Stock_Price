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

import com.ferelin.data_network_api.entities.CryptoPriceApi
import com.ferelin.data_network_api.mappers.CryptoPriceMapper
import com.ferelin.data_network_api.utils.withExceptionHandle
import com.ferelin.domain.entities.CryptoPrice
import com.ferelin.domain.sources.CryptoPriceSource
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_CRYPTO_TOKEN
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CryptoPriceSourceImpl @Inject constructor(
    private val cryptoPriceApi: CryptoPriceApi,
    private val cryptoPriceMapper: CryptoPriceMapper,
    private val dispatchersProvider: DispatchersProvider,
    @Named(NAMED_CRYPTO_TOKEN) private val token: String
) : CryptoPriceSource {

    override suspend fun loadBy(
        cryptoSymbols: List<String>
    ): LoadState<List<CryptoPrice>> = withContext(dispatchersProvider.IO) {
        Timber.d("load (crypto symbols: $cryptoSymbols)")

        val cryptoSymbolsStr = cryptoSymbols.joinToString(separator = ",")

        withExceptionHandle(
            request = {
                cryptoPriceApi
                    .load(cryptoSymbolsStr, token)
                    .execute()
            },
            onSuccess = { responseBody ->
                LoadState.Prepared(
                    data = responseBody.map {
                        cryptoPriceMapper.map(it)
                    }
                )
            },
            onFail = { LoadState.Error() }
        )
    }
}