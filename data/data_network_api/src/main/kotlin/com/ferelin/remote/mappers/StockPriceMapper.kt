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

package com.ferelin.remote.mappers

import com.ferelin.domain.entities.StockPrice
import com.ferelin.remote.entities.StockPriceResponse
import com.ferelin.remote.utils.buildProfitString
import com.ferelin.shared.toStrPrice
import javax.inject.Inject

class StockPriceMapper @Inject constructor() {

    fun map(response: StockPriceResponse): StockPrice {
        return StockPrice(
            currentPrice = response.currentPrice.toStrPrice(),
            previousClosePrice = response.previousClosePrice.toStrPrice(),
            openPrice = response.openPrice.toStrPrice(),
            highPrice = response.highPrice.toStrPrice(),
            lowPrice = response.lowPrice.toStrPrice(),
            profit = buildProfitString(response.currentPrice, response.openPrice)
        )
    }
}