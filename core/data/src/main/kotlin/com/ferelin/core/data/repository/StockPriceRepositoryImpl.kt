package com.ferelin.core.data.repository

import android.util.Log
import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.api.STOCKS_TOKEN
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.mapper.StockPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

internal class StockPriceRepositoryImpl @Inject constructor(
  private val dao: StockPriceDao,
  private val api: StockPriceApi,
  @Named(STOCKS_TOKEN) private val token: String
) : StockPriceRepository {
  override val stockPrice: Flow<List<StockPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(StockPriceMapper::map) }

  override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    checkBackgroundThread()
    try {
      val response = api.load(token, companyTicker)
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