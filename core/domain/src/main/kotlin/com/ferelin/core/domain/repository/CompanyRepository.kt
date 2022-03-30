package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Company
import io.reactivex.rxjava3.core.Observable

interface CompanyRepository {
  val companies: Observable<List<Company>>
}