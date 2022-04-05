package com.ferelin.stockprice.data.mapper

import com.ferelin.stockprice.data.entity.company.CompanyJson
import com.ferelin.stockprice.db.CompanyDBO
import com.ferelin.stockprice.db.ProfileDBO
import com.ferelin.stockprice.domain.entity.Company
import com.ferelin.stockprice.domain.entity.CompanyId

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

  fun map(companiesJson: List<CompanyJson>): List<Pair<CompanyDBO, ProfileDBO>> {
    return companiesJson.mapIndexed { index, json ->
      Pair(
        CompanyDBO(
          id = index,
          name = json.name,
          ticker = json.symbol,
          logoUrl = json.logo
        ),
        ProfileDBO(
          id = index,
          country = json.country,
          phone = json.phone,
          webUrl = json.webUrl,
          industry = json.industry,
          capitalization = json.capitalization
        )
      )
    }
  }
}