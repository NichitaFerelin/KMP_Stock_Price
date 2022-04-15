package com.ferelin.features.stocks.stocks

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.FavoriteCompany

@Immutable
internal data class StockViewData(
    val id: CompanyId,
    val ticker: String,
    val name: String,
    val industry: String,
    val logoUrl: String
)

internal fun Company.toStockViewData(): StockViewData {
    return StockViewData(
        id = this.id,
        ticker = this.ticker,
        name = this.name,
        industry = this.industry,
        logoUrl = this.logoUrl
    )
}

internal fun FavoriteCompany.toStockViewData(): StockViewData {
    return StockViewData(
        id = this.company.id,
        ticker = this.company.ticker,
        name = this.company.name,
        industry = this.company.industry,
        logoUrl = this.company.logoUrl
    )
}