package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.company.CompanyJson
import com.ferelin.core.data.entity.company.CompanyWithFavoriteState
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.FavoriteCompany
import stockprice.CompanyDBO

internal object CompanyMapper {
    fun map(company: Company): CompanyDBO {
        return CompanyDBO(
            id = company.id.value,
            name = company.name,
            ticker = company.ticker,
            industry = company.industry,
            logoUrl = company.logoUrl,
            country = company.country,
            phone = company.phone,
            webUrl = company.webUrl,
            capitalization = company.capitalization,
        )
    }

    fun map(companyDBO: CompanyDBO): Company {
        return Company(
            id = CompanyId(companyDBO.id),
            name = companyDBO.name,
            ticker = companyDBO.ticker,
            industry = companyDBO.industry,
            logoUrl = companyDBO.logoUrl,
            country = companyDBO.country,
            phone = companyDBO.phone,
            webUrl = companyDBO.webUrl,
            capitalization = companyDBO.capitalization,
        )
    }

    fun map(company: CompanyWithFavoriteState): FavoriteCompany {
        return FavoriteCompany(
            company = Company(
                id = CompanyId(company.id),
                name = company.name,
                ticker = company.ticker,
                logoUrl = company.logoUrl,
                country = company.country,
                industry = company.industry,
                phone = company.phone,
                webUrl = company.webUrl,
                capitalization = company.capitalization
            ),
            insertOrder = company.insertOrder
        )
    }

    fun map(companiesJson: List<CompanyJson>): List<CompanyDBO> {
        return companiesJson.mapIndexed { index, companyJson ->
            CompanyDBO(
                id = index,
                name = companyJson.name,
                ticker = companyJson.ticker,
                industry = companyJson.industry,
                logoUrl = companyJson.logoUrl,
                country = companyJson.country,
                phone = companyJson.phone,
                webUrl = companyJson.webUrl,
                capitalization = companyJson.capitalization,
            )
        }
    }
}