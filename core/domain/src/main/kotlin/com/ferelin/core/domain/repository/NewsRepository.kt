package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface NewsRepository {
  fun getAllBy(companyId: CompanyId): Observable<List<News>>
  fun fetchNews(companyId: CompanyId, companyTicker: String) : Completable
}