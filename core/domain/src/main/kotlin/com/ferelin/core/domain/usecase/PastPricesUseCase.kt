package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.repository.PastPriceRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface PastPricesUseCase {
  fun getAllBy(companyId: CompanyId): Observable<List<PastPrice>>
  fun fetchPastPrices(companyId: CompanyId, companyTicker: String)
  val pastPricesLce: Flow<LceState>
}

@Reusable
internal class PastPricesUseCaseImpl @Inject constructor(
  private val pastPriceRepository: PastPriceRepository
) : PastPricesUseCase {
  override fun getAllBy(companyId: CompanyId): Observable<List<PastPrice>> {
    return pastPriceRepository.getAllBy(companyId)
      .doOnSubscribe { pastPricesLceState.value = LceState.Loading }
      .doOnEach { pastPricesLceState.value = LceState.Content }
      .doOnError { e -> pastPricesLceState.value = LceState.Error(e.message) }
  }

  override fun fetchPastPrices(companyId: CompanyId, companyTicker: String) {
    pastPriceRepository.fetchPastPrices(companyId, companyTicker)
      .doOnSubscribe { pastPricesLceState.value = LceState.Loading }
      .doOnComplete { pastPricesLceState.value = LceState.Content }
      .doOnError { e -> pastPricesLceState.value = LceState.Error(e.message) }
      .blockingAwait()
  }

  private val pastPricesLceState = MutableStateFlow<LceState>(LceState.None)
  override val pastPricesLce: Flow<LceState> = pastPricesLceState.asStateFlow()
}

