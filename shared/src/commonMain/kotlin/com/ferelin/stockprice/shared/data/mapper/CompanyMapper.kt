package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.db.CompanyDBO
import com.ferelin.stockprice.db.ProfileDBO
import com.ferelin.stockprice.shared.data.entity.company.CompanyPojo
import com.ferelin.stockprice.shared.domain.entity.Company
import com.ferelin.stockprice.shared.domain.entity.CompanyId

internal object CompanyMapper {
    fun map(company: Company): CompanyDBO {
        return CompanyDBO(
            id = company.id.value,
            name = company.name,
            ticker = company.ticker,
            logoUrl = company.logoUrl
        )
    }

    fun map(companyDBO: CompanyDBO): Company {
        return Company(
            id = CompanyId(companyDBO.id),
            name = companyDBO.name,
            ticker = companyDBO.ticker,
            logoUrl = companyDBO.logoUrl
        )
    }

    fun map(companiesPojo: List<CompanyPojo>): Pair<List<CompanyDBO>, List<ProfileDBO>> {
        return companiesPojo.mapIndexed { index, pojo ->
            Pair(
                CompanyDBO(
                    id = index,
                    name = pojo.name,
                    ticker = pojo.symbol,
                    logoUrl = pojo.logo
                ),
                ProfileDBO(
                    id = index,
                    country = pojo.country,
                    phone = pojo.phone,
                    webUrl = pojo.webUrl,
                    industry = pojo.industry,
                    capitalization = pojo.capitalization
                )
            )
        }.unzip()
    }
}