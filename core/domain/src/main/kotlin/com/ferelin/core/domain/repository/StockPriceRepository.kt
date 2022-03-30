package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface StockPriceRepository {
  val stockPrice: Observable<List<StockPrice>>
  fun fetchPrice(companyId: CompanyId, companyTicker: String): Completable
}