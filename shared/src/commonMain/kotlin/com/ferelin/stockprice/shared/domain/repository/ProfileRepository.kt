package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
  fun getBy(companyId: CompanyId): Flow<Profile>
}