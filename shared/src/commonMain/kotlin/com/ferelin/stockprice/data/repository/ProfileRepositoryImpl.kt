package com.ferelin.stockprice.data.repository

import com.ferelin.stockprice.data.entity.profile.ProfileDao
import com.ferelin.stockprice.data.mapper.ProfileMapper
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.Profile
import com.ferelin.common.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class ProfileRepositoryImpl(
  private val dao: ProfileDao
) : ProfileRepository {
  override fun getBy(companyId: CompanyId): Flow<Profile> {
    return dao.getBy(companyId.value)
      .distinctUntilChanged()
      .map(ProfileMapper::map)
  }
}