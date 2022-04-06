package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.androidApp.data.entity.profile.ProfileDao
import com.ferelin.stockprice.androidApp.data.mapper.ProfileMapper
import com.ferelin.stockprice.androidApp.domain.entity.CompanyId
import com.ferelin.stockprice.androidApp.domain.entity.Profile
import com.ferelin.stockprice.androidApp.domain.repository.ProfileRepository
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