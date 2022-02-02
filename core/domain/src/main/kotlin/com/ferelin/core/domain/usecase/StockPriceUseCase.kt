package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import dagger.Reusable
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface StockPriceUseCase {
  val stockPrice: Flow<List<StockPrice>>
  suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
  val stockPriceLce: Flow<LceState>
}

@Reusable
internal class StockPriceUseCaseImpl @Inject constructor(
  private val stockPriceRepository: StockPriceRepository,
  dispatchersProvider: DispatchersProvider
) : StockPriceUseCase {
  override val stockPrice: Flow<List<StockPrice>> = stockPriceRepository.stockPrice
    .zip(
      other = stockPriceRepository.fetchError,
      transform = { stockPrices, exception ->
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