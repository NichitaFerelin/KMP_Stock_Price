package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.FavoriteCompany
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    val companies: Flow<List<Company>>
    val favoriteCompanies: Flow<List<FavoriteCompany>>
    suspend fun addToFavorites(id: CompanyId)
    suspend fun eraseFromFavorites(id: CompanyId)
}