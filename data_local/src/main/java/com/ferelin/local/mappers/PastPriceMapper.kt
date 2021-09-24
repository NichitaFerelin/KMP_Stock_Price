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

package com.ferelin.local.mappers

import com.ferelin.domain.entities.PastPrice
import com.ferelin.local.entities.PastPriceDBO

class PastPriceMapper {

    fun map(pastPrice: PastPrice): PastPriceDBO {
        return PastPriceDBO(
            id = pastPrice.id,
            relationId = pastPrice.relationId,
            openPrice = pastPrice.openPrice,
            highPrice = pastPrice.highPrice,
            lowPrice = pastPrice.lowPrice,
            closePrice = pastPrice.closePrice,
            date = pastPrice.date
        )
    }

    fun map(dbo: PastPriceDBO): PastPrice {
        return PastPrice(
            id = dbo.id,
            relationId = dbo.relationId,
            openPrice = dbo.openPrice,
            highPrice = dbo.highPrice,
            lowPrice = dbo.lowPrice,
            closePrice = dbo.closePrice,
            date = dbo.date
        )
    }
}