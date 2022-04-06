package com.ferelin.stockprice.shared.commonMain.data.repository

import com.ferelin.stockprice.shared.commonMain.data.entity.profile.ProfileDao
import com.ferelin.stockprice.shared.commonMain.data.mapper.ProfileMapper
import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.Profile
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