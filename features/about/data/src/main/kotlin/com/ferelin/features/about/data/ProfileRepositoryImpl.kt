package com.ferelin.features.about.data

import com.ferelin.core.data.entity.ProfileDao
import com.ferelin.core.data.mapper.ProfileMapper
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.Profile
import com.ferelin.features.about.domain.repositories.ProfileRepository
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