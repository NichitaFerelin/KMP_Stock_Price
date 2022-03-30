package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface StockPriceUseCase {
  val stockPrice: Observable<List<StockPrice>>
  val stockPriceLce: Flow<LceState>
  fun fetchPrice(companyId: CompanyId, companyTicker: String)
}

@Reusable
internal class StockPriceUseCaseImpl @Inject constructor(
  private val stockPriceRepository: StockPriceRepository
) : StockPriceUseCase {
  override val stockPrice: Observable<List<StockPrice>> = stockPriceRepository.stockPrice
    .doOnSubscribe { stockPriceLceState.value = LceState.Loading }
    .doOnEach { stockPriceLceState.value = LceState.Content }
    .doOnError { e -> stockPriceLceState.value = LceState.Error(e.message) }

  override fun fetchPrice(companyId: CompanyId, companyTicker: String) {
    stockPriceRepository.fetchPrice(companyId, companyTicker)
      .doOnSubscribe { stockPriceLceState.value = LceState.Loading }
      .doOnComplete { stockPriceLceState.value = LceState.Content }
      .doOnError { e -> stockPriceLceState.value = LceState.Error(e.message) }
      .blockingAwait()
  }

  private val stockPriceLceState = MutableStateFlow<LceState>(LceState.None)
  override val stockPriceLce: Flow<LceState> = stockPriceLceState.asStateFlow()
}