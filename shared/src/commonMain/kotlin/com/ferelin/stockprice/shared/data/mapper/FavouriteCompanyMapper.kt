package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.db.FavouriteCompanyDBO
import com.ferelin.stockprice.shared.domain.entity.CompanyId

internal object FavouriteCompanyMapper {
  fun map(companyId: CompanyId): FavouriteCompanyDBO {
    return FavouriteCompanyDBO(companyId.value)
  }

  fun map(favouriteCompanyId: Int): CompanyId {
    return CompanyId(favouriteCompanyId)
  }
}