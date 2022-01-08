package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.mapper.ProfileMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.Profile
import com.ferelin.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ProfileRepositoryImpl @Inject constructor(
  private val dao: ProfileDao
) : ProfileRepository {
  override fun getBy(companyId: CompanyId): Flow<Profile> {
    return dao.getBy(companyId.value)
      .distinctUntilChanged()
      .map(ProfileMapper::map)
  }
}