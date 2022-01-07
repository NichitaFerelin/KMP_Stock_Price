package com.ferelin.features.about.domain.repositories

import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
  fun getBy(companyId: CompanyId): Flow<Profile>
}