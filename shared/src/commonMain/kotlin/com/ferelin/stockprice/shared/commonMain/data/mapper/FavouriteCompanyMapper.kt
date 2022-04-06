package com.ferelin.stockprice.shared.commonMain.data.mapper

import com.ferelin.stockprice.db.FavouriteCompanyDBO
import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId

internal object FavouriteCompanyMapper {
  fun map(companyId: CompanyId): FavouriteCompanyDBO {
    return FavouriteCompanyDBO(companyId.value)
  }

  fun map(favouriteCompanyId: Int): CompanyId {
    return CompanyId(favouriteCompanyId)
  }
}