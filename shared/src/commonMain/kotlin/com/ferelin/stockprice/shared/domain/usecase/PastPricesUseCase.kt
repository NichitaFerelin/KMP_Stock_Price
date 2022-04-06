package com.ferelin.stockprice.shared.domain.usecase

import com.ferelin.stockprice.androidApp.domain.entity.CompanyId
import com.ferelin.stockprice.androidApp.domain.entity.LceState
import com.ferelin.stockprice.androidApp.domain.entity.PastPrice
import com.ferelin.stockprice.androidApp.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.*

interface PastPricesUseCase {
  fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
  suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String)
  val pastPricesLce: Flow<LceState>
}

internal class PastPricesUseCaseImpl(
  private val pastPriceRepository: PastPriceRepository
) : PastPricesUseCase {
  override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
    return pastPriceRepository.getAllBy(companyId)
      .onStart { pastPricesLceState.value = LceState.Loading }
      .onEach { pastPricesLceState.value = LceState.Content }
      .catch { e -> pastPricesLceState.value = LceState.Error(e.message) }
  }

  override suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String) {
    pastPriceRepository.fetchPastPrices(companyId, companyTicker)
  }

  private val pastPricesLceState = MutableStateFlow<LceState>(LceState.None)
  override val pastPricesLce: Flow<LceState> = pastPricesLceState.asStateFlow()
}

