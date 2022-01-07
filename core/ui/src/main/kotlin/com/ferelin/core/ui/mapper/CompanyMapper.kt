package com.ferelin.core.ui.mapper

import com.ferelin.core.domain.entities.entity.Company
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider

internal object CompanyMapper {
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
        style = null,
        stockPriceViewData = null
      )
    }
  }
}