package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDBO
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyResponse
import com.ferelin.core.domain.entity.CompanyId

internal object FavouriteCompanyMapper {
  fun map(companyId: CompanyId): FavouriteCompanyDBO {
    return FavouriteCompanyDBO(companyId.value)
  }

  fun map(favouriteCompanyDBO: FavouriteCompanyDBO): CompanyId {
    return CompanyId(favouriteCompanyDBO.id)
  }

  fun map(favouriteCompanyResponse: FavouriteCompanyResponse): List<FavouriteCompanyDBO> {
    return favouriteCompanyResponse.data.map { FavouriteCompanyDBO(it) }
  }
}