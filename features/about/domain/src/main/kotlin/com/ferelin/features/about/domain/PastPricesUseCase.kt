package com.ferelin.features.about.domain

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.features.about.domain.entities.PastPrice
import com.ferelin.features.about.domain.repositories.PastPricesRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface PastPricesUseCase {
  fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
  suspend fun fetchPastPrices(companyId: CompanyId)
  val pastPricesLce: Flow<LceState>
}

internal class PastPricesUseCaseImpl @Inject constructor(
  private val pastPricesRepository: PastPricesRepository,
  private val dispatchersProvider: DispatchersProvider
) : PastPricesUseCase {
  override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
    return pastPricesRepository.getAllBy(companyId)
      .onStart { pastPricesLceState.value = LceState.Loading }
      .onEach { pastPricesLceState.value = LceState.Content }
      .catch { e -> pastPricesLceState.value = LceState.Error(e.message) }
      .flowOn(dispatchersProvider.IO)
  }

  override suspend fun fetchPastPrices(companyId: CompanyId) {
    TODO("Not yet implemented")
  }

  private val pastPricesLceState = MutableStateFlow<LceState>(LceState.None)
  override val pastPricesLce: Flow<LceState> = pastPricesLceState.asStateFlow()
}

