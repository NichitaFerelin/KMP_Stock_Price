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

package com.ferelin.feature_section_stocks.mapper

import com.ferelin.core.utils.toStrPrice
import com.ferelin.domain.entities.CryptoWithPrice
import com.ferelin.feature_section_stocks.utils.formatProfitString
import com.ferelin.feature_section_stocks.viewData.CryptoViewData
import com.ferelin.feature_stocks_default.R
import javax.inject.Inject

class CryptoWithPriceMapper @Inject constructor() {

    private val profitPlus = R.color.profitPlus
    private val profitMinus = R.color.profitMinus

    fun map(cryptoWithPrice: CryptoWithPrice): CryptoViewData {
        var price = "-"
        var priceProfit = "-"
        var priceProfitPercents = "-"

        cryptoWithPrice.cryptoPrice?.let {
            price = it.price.toStrPrice()
            priceProfit = it.priceChange.toString()
            priceProfitPercents = it.priceChangePercents.toString()
        }

        val profitStr = formatProfitString(priceProfit, priceProfitPercents)

        return CryptoViewData(
            id = cryptoWithPrice.crypto.id,
            name = cryptoWithPrice.crypto.name,
            logoUrl = cryptoWithPrice.crypto.logoUrl,
            price = price,
            profit = profitStr,
            profitColor = if (profitStr.getOrNull(0) == '-') {
                profitMinus
            } else {
                profitPlus
            }
        )
    }
}