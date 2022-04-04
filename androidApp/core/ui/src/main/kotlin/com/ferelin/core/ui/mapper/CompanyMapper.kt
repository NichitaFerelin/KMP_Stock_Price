package com.ferelin.core.ui.mapper

import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.viewData.StockViewData

object CompanyMapper {
  fun map(
    companies: List<Company>,
    favouriteCompaniesIds: List<CompanyId>
  ): List<StockViewData> {
    val favouriteCompaniesContainer = favouriteCompaniesIds.associateWith { /**/ }
    return companies.map { company ->
      val isFavourite = favouriteCompaniesContainer[company.id] != null
      StockViewData(
        id = company.id,
        name = company.name,
        ticker = company.ticker,
        logoUrl = company.logoUrl,
        isFavourite = isFavourite,
        stockPriceViewData = null
      )
    }
  }
}