package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
  fun getBy(companyId: CompanyId): Flow<Profile>
}