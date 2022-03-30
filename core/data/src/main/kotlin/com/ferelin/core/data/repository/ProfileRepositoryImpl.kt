package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.mapper.ProfileMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.Profile
import com.ferelin.core.domain.repository.ProfileRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class ProfileRepositoryImpl @Inject constructor(
  private val dao: ProfileDao
) : ProfileRepository {
  override fun getBy(companyId: CompanyId): Observable<Profile> {
    return dao.getBy(companyId.value)
      .distinctUntilChanged()
      .map(ProfileMapper::map)
  }
}