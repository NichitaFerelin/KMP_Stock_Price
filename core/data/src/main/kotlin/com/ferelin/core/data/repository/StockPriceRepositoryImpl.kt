package com.ferelin.core.data.repository

import com.ferelin.core.data.api.STOCKS_TOKEN
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.mapper.StockPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Named

internal class StockPriceRepositoryImpl @Inject constructor(
  private val dao: StockPriceDao,
  private val api: StockPriceApi,
  @Named(STOCKS_TOKEN) private val token: String
) : StockPriceRepository {
  override val stockPrice: Observable<List<StockPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(StockPriceMapper::map) }

  override fun fetchPrice(companyId: CompanyId, companyTicker: String): Completable {
    return try {
      val response = api.load(token, companyTicker).blockingGet()
      dao.insert(
        stockPriceDBO = StockPriceMapper.map(response, companyId)
      )
      Completable.complete()
    } catch (e: Exception) {
      Completable.error(e)
    }
  }
}