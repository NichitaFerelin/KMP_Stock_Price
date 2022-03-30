package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.PastPrice
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface PastPriceRepository {
  fun getAllBy(companyId: CompanyId): Observable<List<PastPrice>>
  fun fetchPastPrices(companyId: CompanyId, companyTicker: String) : Completable
}