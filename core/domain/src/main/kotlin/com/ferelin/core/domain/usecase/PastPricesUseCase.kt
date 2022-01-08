package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface PastPricesUseCase {
  fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
  suspend fun fetchPastPrices(companyId: CompanyId)
  val pastPricesLce: Flow<LceState>
}

internal class PastPricesUseCaseImpl @Inject constructor(
  private val pastPriceRepository: PastPriceRepository,
  private val dispatchersProvider: DispatchersProvider
) : PastPricesUseCase {
  override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
    return pastPriceRepository.getAllBy(companyId)
      .onStart { pastPricesLceState.value = LceState.Loading }
      .onEach { pastPricesLceState.value = LceState.Content }
      .catch { e -> pastPricesLceState.value = LceState.Error(e.message) }
      .flowOn(dispatchersProvider.IO)
  }

  override suspend fun fetchPastPrices(companyId: CompanyId) {
    // todo
  }

  private val pastPricesLceState = MutableStateFlow<LceState>(LceState.None)
  override val pastPricesLce: Flow<LceState> = pastPricesLceState.asStateFlow()
}

