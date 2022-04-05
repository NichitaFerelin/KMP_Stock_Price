package com.ferelin.core.ui.mapper

import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.stockprice.domain.entity.Company
import com.ferelin.stockprice.domain.entity.CompanyId

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