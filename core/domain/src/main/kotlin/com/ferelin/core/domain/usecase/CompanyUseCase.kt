package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CompanyUseCase {
  val companies: Flow<List<Company>>
  val companiesLce: Flow<LceState>
}

internal class CompanyUseCaseImpl @Inject constructor(
  private val companyRepository: CompanyRepository,
  private val dispatchersProvider: DispatchersProvider
) : CompanyUseCase {
  override val companies: Flow<List<Company>>
    get() = companyRepository.companies
      .onStart { companiesLceState.value = LceState.Loading }
      .onEach { companiesLceState.value = LceState.Content }
      .catch { e -> companiesLceState.value = LceState.Error(e.message) }
      .flowOn(dispatchersProvider.IO)

  private val companiesLceState = MutableStateFlow<LceState>(LceState.None)
  override val companiesLce: Flow<LceState> = companiesLceState.asStateFlow()
}