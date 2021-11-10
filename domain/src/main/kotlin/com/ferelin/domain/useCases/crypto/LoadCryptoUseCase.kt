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

package com.ferelin.domain.useCases.crypto

import com.ferelin.domain.entities.CryptoPrice
import com.ferelin.domain.entities.CryptoWithPrice
import com.ferelin.domain.repositories.CryptoPriceRepo
import com.ferelin.domain.sources.CryptoPriceSource
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class LoadCryptoUseCase @Inject constructor(
    private val cryptoPriceSource: CryptoPriceSource,
    private val cryptoPriceRepo: CryptoPriceRepo,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    suspend fun loadInto(cryptosWithPrice: List<CryptoWithPrice>): Boolean {
        val cryptosSymbols = cryptosWithPrice.map { it.crypto.symbol }
        val loadState = cryptoPriceSource.load(cryptosSymbols)

        updateIfPrepared(cryptosWithPrice, loadState)

        return loadState.ifPrepared {
            true
        } ?: false
    }

    private fun updateIfPrepared(
        sourceCryptoWithPrice: List<CryptoWithPrice>,
        loadState: LoadState<List<CryptoPrice>>
    ) {
        loadState.ifPrepared { preparedState ->
            sourceCryptoWithPrice.forEach { cryptoWithPrice ->

                preparedState
                    .data
                    .find { cryptoWithPrice.crypto.symbol == it.relationCryptoSymbol }
                    ?.let {
                        // set relations data to cache
                        it.relationCryptoId = cryptoWithPrice.crypto.id

                        // update source crypto
                        cryptoWithPrice.cryptoPrice = it
                    }
            }

            externalScope.launch { cryptoPriceRepo.insertAll(preparedState.data) }
        }
    }
}