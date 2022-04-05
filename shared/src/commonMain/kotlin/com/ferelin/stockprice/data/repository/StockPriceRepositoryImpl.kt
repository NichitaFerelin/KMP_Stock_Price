package com.ferelin.stockprice.data.repository

import com.ferelin.stockprice.data.entity.stockPrice.StockPriceApi
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceDao
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceOptions
import com.ferelin.stockprice.data.mapper.StockPriceMapper
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.StockPrice
import com.ferelin.common.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.*

internal class StockPriceRepositoryImpl(
  private val dao: StockPriceDao,
  private val api: StockPriceApi,
  private val token: String
) : StockPriceRepository {
  override val stockPrice: Flow<List<StockPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(StockPriceMapper::map) }

  override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    try {
      val options =
        StockPriceOptions(token, companyTicker)
      val response = api.load(options)
      dao.insert(stockPriceDBO = StockPriceMapper.map(response, companyId))
      fetchErrorState.emit(null)
    } catch (e: Exception) {
      fetchErrorState.emit(e)
    }
  }

  private val fetchErrorState = MutableSharedFlow<Exception?>()
  override val fetchError: Flow<Exception?> = fetchErrorState.asSharedFlow()
}