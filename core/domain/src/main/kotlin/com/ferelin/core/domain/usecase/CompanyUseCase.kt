package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CompanyRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface CompanyUseCase {
  val companies: Observable<List<Company>>
  val companiesLce: Flow<LceState>
}

@Reusable
internal class CompanyUseCaseImpl @Inject constructor(
  private val companyRepository: CompanyRepository
) : CompanyUseCase {
  override val companies: Observable<List<Company>>
    get() = companyRepository.companies
      .doOnSubscribe { companiesLceState.value = LceState.Loading }
      .doOnEach { companiesLceState.value = LceState.Content }
      .doOnError { e -> companiesLceState.value = LceState.Error(e.message) }

  private val companiesLceState = MutableStateFlow<LceState>(LceState.None)
  override val companiesLce: Flow<LceState> = companiesLceState.asStateFlow()
}