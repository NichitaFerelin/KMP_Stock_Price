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

import com.ferelin.data_network_api.entities.PastPricesResponse
import com.ferelin.data_network_api.utils.toBasicMillisTime
import com.ferelin.domain.entities.PastPrice
import javax.inject.Inject

class PastPriceMapper @Inject constructor() {

    fun map(pastPriceResponse: PastPricesResponse, companyId: Int): List<PastPrice> {
        with(pastPriceResponse) {
            if (
                closePrices.size != highPrices.size
                || highPrices.size != lowPrices.size
                || lowPrices.size != openPrices.size
                || openPrices.size != timestamps.size
                || timestamps.size != closePrices.size
            ) return emptyList()

            return List(timestamps.size) { index ->
                val openPrice = openPrices[index]
                val highPrice = highPrices[index]
                val lowPrice = lowPrices[index]
                val closePrice = closePrices[index]
                val timestamp = timestamps[index]

                PastPrice(
                    relationCompanyId = companyId,
                    openPrice = openPrice,
                    highPrice = highPrice,
                    lowPrice = lowPrice,
                    closePrice = closePrice,
                    dateMillis = timestamp.toBasicMillisTime()
                )
            }
        }
    }
}