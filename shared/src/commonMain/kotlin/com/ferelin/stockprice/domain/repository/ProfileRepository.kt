package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
  fun getBy(companyId: CompanyId): Flow<Profile>
}