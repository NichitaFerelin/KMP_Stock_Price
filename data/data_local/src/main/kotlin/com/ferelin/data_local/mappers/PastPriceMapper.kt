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

import com.ferelin.data_local.entities.PastPriceDBO
import com.ferelin.domain.entities.PastPrice
import javax.inject.Inject

class PastPriceMapper @Inject constructor() {

    fun map(pastPrice: PastPrice): PastPriceDBO {
        return PastPriceDBO(
            id = pastPrice.id,
            relationCompanyId = pastPrice.relationCompanyId,
            openPrice = pastPrice.openPrice,
            highPrice = pastPrice.highPrice,
            lowPrice = pastPrice.lowPrice,
            closePrice = pastPrice.closePrice,
            dateMillis = pastPrice.dateMillis
        )
    }

    fun map(pastPriceDBO: PastPriceDBO): PastPrice {
        return PastPrice(
            id = pastPriceDBO.id,
            relationCompanyId = pastPriceDBO.relationCompanyId,
            openPrice = pastPriceDBO.openPrice,
            highPrice = pastPriceDBO.highPrice,
            lowPrice = pastPriceDBO.lowPrice,
            closePrice = pastPriceDBO.closePrice,
            dateMillis = pastPriceDBO.dateMillis
        )
    }
}