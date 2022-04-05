package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.CompanyId
import kotlinx.coroutines.flow.Flow

interface FavouriteCompanyRepository {
  val favouriteCompanies: Flow<List<CompanyId>>
  suspend fun addToFavourite(companyId: CompanyId)
  suspend fun removeFromFavourite(companyId: CompanyId)
  suspend fun eraseAll(clearCloud: Boolean)
}