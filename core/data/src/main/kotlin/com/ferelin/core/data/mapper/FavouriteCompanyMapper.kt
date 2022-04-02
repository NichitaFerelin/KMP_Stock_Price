package com.ferelin.core.data.mapper

import com.ferelin.core.domain.entity.CompanyId
import stockprice.FavouriteCompanyDBO

internal object FavouriteCompanyMapper {
  fun map(companyId: CompanyId): FavouriteCompanyDBO {
    return FavouriteCompanyDBO(companyId.value)
  }

  fun map(favouriteCompanyId: Int): CompanyId {
    return CompanyId(favouriteCompanyId)
  }
}