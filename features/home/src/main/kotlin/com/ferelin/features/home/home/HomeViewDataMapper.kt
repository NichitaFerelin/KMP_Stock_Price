package com.ferelin.features.home.home

import com.ferelin.core.domain.entity.Company

object HomeViewDataMapper {
    fun map(company: Company): HomeStockViewData {
        return HomeStockViewData(
            id = company.id.value,
            name = company.name,
            industry = company.industry,
            isFavourite = company.isFavourite,
            logoUrl = company.logoUrl
        )
    }
}