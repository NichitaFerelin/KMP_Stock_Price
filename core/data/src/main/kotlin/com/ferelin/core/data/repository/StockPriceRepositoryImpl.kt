package com.ferelin.core.data.repository

import com.ferelin.core.data.di.StocksToken
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.entity.stockPrice.StockPriceOptions
import com.ferelin.core.data.mapper.StockPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class StockPriceRepositoryImpl @Inject constructor(
  private val dao: StockPriceDao,
  private val api: StockPriceApi,
  @StocksToken private val token: String
) : StockPriceRepository {
  override val stockPrice: Flow<List<StockPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(StockPriceMapper::map) }

  override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    try {
      val requestOptions = StockPriceOptions(token, companyTicker)
      val response = api.load(requestOptions)
      dao.insert(stockPriceDBO = StockPriceMapper.map(response, companyId))
      fetchErrorState.emit(null)
    } catch (e: Exception) {
      fetchErrorState.emit(e)
    }
  }

  private val fetchErrorState = MutableSharedFlow<Exception?>()
  override val fetchError: Flow<Exception?> = fetchErrorState.asSharedFlow()
}