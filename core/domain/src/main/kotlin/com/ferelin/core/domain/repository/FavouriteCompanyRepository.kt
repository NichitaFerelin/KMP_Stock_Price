package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import io.reactivex.rxjava3.core.Observable

interface FavouriteCompanyRepository {
  val favouriteCompanies: Observable<List<CompanyId>>
  fun addToFavourite(companyId: CompanyId)
  fun removeFromFavourite(companyId: CompanyId)
  fun eraseAll(clearCloud: Boolean)
}