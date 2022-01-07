package com.ferelin.core.data

import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.mapper.StockPriceMapper
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.StockPrice
import com.ferelin.core.domain.entities.repository.StockPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class StockPriceRepositoryImpl @Inject constructor(
  private val dao: StockPriceDao,
  private val api: StockPriceApi
) : StockPriceRepository {
  override val stockPrice: Flow<List<StockPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(StockPriceMapper::map) }

  override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    checkBackgroundThread()
    try {
      val response = api.load(companyTicker)
      dao.insert(
        stockPriceDBO = StockPriceMapper.map(response, companyId)
      )
      fetchErrorState.emit(null)
    } catch (e: Exception) {
      fetchErrorState.emit(e)
    }
  }

  private val fetchErrorState = MutableSharedFlow<Exception?>()
  override val fetchError: Flow<Exception?> = fetchErrorState.asSharedFlow()
}