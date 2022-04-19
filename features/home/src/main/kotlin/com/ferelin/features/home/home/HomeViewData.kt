package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.FavoriteCompany

@Immutable
internal data class HomeStockViewData(
    val id: Int,
    val name: String,
    val industry: String,
    val isFavorite: Boolean,
    val logoUrl: String
)

internal fun Company.toHomeStockViewData(): HomeStockViewData {
    return HomeStockViewData(
        id = this.id.value,
        name = this.name,
        industry = this.industry,
        isFavorite = false,
        logoUrl = this.logoUrl
    )
}

internal fun FavoriteCompany.toHomeStockViewData(): HomeStockViewData {
    return HomeStockViewData(
        id = this.company.id.value,
        name = this.company.name,
        industry = this.company.industry,
        isFavorite = true,
        logoUrl = this.company.logoUrl
    )
}