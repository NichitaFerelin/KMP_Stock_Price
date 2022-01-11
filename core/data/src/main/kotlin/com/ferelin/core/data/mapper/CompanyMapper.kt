package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.profile.ProfileDBO
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.data.entity.company.CompanyDBO
import com.ferelin.core.data.entity.company.CompanyJson

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