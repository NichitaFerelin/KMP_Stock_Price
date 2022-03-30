package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.Profile
import io.reactivex.rxjava3.core.Observable

interface ProfileRepository {
  fun getBy(companyId: CompanyId): Observable<Profile>
}