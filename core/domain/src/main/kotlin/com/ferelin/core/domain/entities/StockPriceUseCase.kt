package com.ferelin.core.domain.entities

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.core.domain.entities.entity.StockPrice
import com.ferelin.core.domain.entities.repository.StockPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface StockPriceUseCase {
  val stockPrice: Flow<List<StockPrice>>
  suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
  val stockPriceLce: Flow<LceState>
}

internal class StockPriceUseCaseImpl @Inject constructor(
  private val stockPriceRepository: StockPriceRepository,
  dispatchersProvider: DispatchersProvider
) : StockPriceUseCase {
  override val stockPrice: Flow<List<StockPrice>> = stockPriceRepository.fetchError
    .combine(
      flow = stockPriceRepository.stockPrice,
      transform = { exception, stockPrices ->
        if (exception != null) {
          stockPriceLceState.value = LceState.Error(exception.message)
        }
        stockPrices
      }
    )
    .onStart { stockPriceLceState.value = LceState.Loading }
    .onEach { stockPriceLceState.value = LceState.Content }
    .catch { e -> stockPriceLceState.value = LceState.Error(e.message) }
    .flowOn(dispatchersProvider.IO)

  override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    stockPriceRepository.fetchPrice(companyId, companyTicker)
  }

  private val stockPriceLceState = MutableStateFlow<LceState>(LceState.None)
  override val stockPriceLce: Flow<LceState> = stockPriceLceState.asStateFlow()
}