package com.ferelin.stockprice.shared.commonMain.ui.mapper

import com.ferelin.stockprice.shared.commonMain.domain.entity.Company
import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.ui.viewData.StockViewData

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