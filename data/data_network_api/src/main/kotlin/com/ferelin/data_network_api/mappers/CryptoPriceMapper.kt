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

package com.ferelin.data_network_api.mappers

import com.ferelin.data_network_api.entities.CryptoPriceResponse
import com.ferelin.domain.entities.CryptoPrice
import javax.inject.Inject

class CryptoPriceMapper @Inject constructor() {

    fun map(cryptoPriceResponse: CryptoPriceResponse): CryptoPrice {
        return CryptoPrice(
            relationCryptoSymbol = cryptoPriceResponse.relationSymbol,
            price = cryptoPriceResponse.price.toDoubleOrNull() ?: 0.0,
            priceTimestamp = cryptoPriceResponse.priceTimestamp,
            highPrice = cryptoPriceResponse.highPrice.toDoubleOrNull() ?: 0.0,
            highPriceTimestamp = cryptoPriceResponse.highPriceTimestamp,
            priceChange = cryptoPriceResponse
                .priceChangeInfo
                .priceChange
                .toDoubleOrNull() ?: 0.0,
            priceChangePercents = cryptoPriceResponse
                .priceChangeInfo
                .priceChangePercents
                .toDoubleOrNull() ?: 0.0
        )
    }
}