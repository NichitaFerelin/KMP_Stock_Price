package com.ferelin.features.home.home

import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.FavoriteCompany

internal object HomeViewDataMapper {
    fun map(company: Company): HomeStockViewData {
        return HomeStockViewData(
            id = company.id.value,
            name = company.name,
            industry = company.industry,
            isFavorite = false,
            logoUrl = company.logoUrl
        )
    }

    fun map(favoriteCompany: FavoriteCompany): HomeStockViewData {
        return HomeStockViewData(
            id = favoriteCompany.company.id.value,
            name = favoriteCompany.company.name,
            industry = favoriteCompany.company.industry,
            isFavorite = true,
            logoUrl = favoriteCompany.company.logoUrl
        )
    }
}