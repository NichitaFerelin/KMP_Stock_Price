package com.ferelin.core.domain.entities.repository

import com.ferelin.core.domain.entities.entity.CompanyId
import kotlinx.coroutines.flow.Flow

interface FavouriteCompanyRepository {
  val favouriteCompanies: Flow<List<CompanyId>>
  suspend fun addToFavourite(companyId: CompanyId)
  suspend fun removeFromFavourite(companyId: CompanyId)
  suspend fun eraseAll(clearCloud: Boolean)
}