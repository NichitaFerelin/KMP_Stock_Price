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

package com.ferelin.data_local.mappers

import com.ferelin.data_local.entities.CryptoPriceDBO
import com.ferelin.data_local.entities.CryptoWithPriceDBO
import com.ferelin.domain.entities.CryptoPrice
import com.ferelin.domain.entities.CryptoWithPrice
import javax.inject.Inject

class CryptoPriceMapper @Inject constructor(
    private val cryptoMapper: CryptoMapper
) {
    fun map(cryptoPrice: CryptoPrice): CryptoPriceDBO {
        return CryptoPriceDBO(
            relationCryptoId = cryptoPrice.relationCryptoId,
            price = cryptoPrice.price,
            priceTimestamp = cryptoPrice.priceTimestamp,
            highPrice = cryptoPrice.highPrice,
            highPriceTimestamp = cryptoPrice.highPriceTimestamp,
            priceChange = cryptoPrice.priceChange,
            priceChangePercents = cryptoPrice.priceChangePercents
        )
    }

    fun map(cryptoWithPriceDBO: CryptoWithPriceDBO): CryptoWithPrice {
        return CryptoWithPrice(
            crypto = cryptoMapper.map(cryptoWithPriceDBO.cryptoDBO),
            cryptoPrice = cryptoWithPriceDBO.cryptoPriceDBO?.let { map(it) }
        )
    }

    private fun map(cryptoPriceDBO: CryptoPriceDBO): CryptoPrice {
        return CryptoPrice(
            relationCryptoId = cryptoPriceDBO.relationCryptoId,
            price = cryptoPriceDBO.price,
            priceTimestamp = cryptoPriceDBO.priceTimestamp,
            highPrice = cryptoPriceDBO.highPrice,
            highPriceTimestamp = cryptoPriceDBO.highPriceTimestamp,
            priceChange = cryptoPriceDBO.priceChange,
            priceChangePercents = cryptoPriceDBO.priceChangePercents
        )
    }
}