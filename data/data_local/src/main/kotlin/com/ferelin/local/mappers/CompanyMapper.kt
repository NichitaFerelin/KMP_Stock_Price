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

import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.local.entities.CompanyDBO
import com.ferelin.local.entities.CompanyWithStockPriceDBO
import com.ferelin.local.utils.CompanyPojo
import javax.inject.Inject

class CompanyMapper @Inject constructor(
    private val mStockPriceMapper: StockPriceMapper
) {

    fun map(dbo: CompanyDBO): Company {
        return Company(
            id = dbo.id,
            name = dbo.name,
            ticker = dbo.ticker,
            logoUrl = dbo.logoUrl,
            isFavourite = dbo.isFavourite,
            addedByIndex = dbo.addedByIndex
        )
    }

    fun map(company: Company): CompanyDBO {
        return CompanyDBO(
            id = company.id,
            name = company.name,
            ticker = company.ticker,
            logoUrl = company.logoUrl,
            isFavourite = company.isFavourite,
            addedByIndex = company.addedByIndex
        )
    }

    fun map(pojoIndex: Int, pojo: CompanyPojo): Company {
        return Company(
            id = pojoIndex,
            name = pojo.name,
            ticker = pojo.symbol,
            logoUrl = pojo.logo,
        )
    }

    fun map(companyWithStockPriceDBO: CompanyWithStockPriceDBO): CompanyWithStockPrice {
        return CompanyWithStockPrice(
            company = map(companyWithStockPriceDBO.companyDBO),
            stockPrice = companyWithStockPriceDBO
                .stockPriceDBO
                ?.let { mStockPriceMapper.map(it) }
        )
    }
}