package com.ferelin.stockprice.shared.domain.usecase

import com.ferelin.stockprice.shared.domain.entity.Company
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.*

interface CompanyUseCase {
  val companies: Flow<List<Company>>
  val companiesLce: Flow<LceState>
}

internal class CompanyUseCaseImpl(
  private val companyRepository: CompanyRepository,
) : CompanyUseCase {
  override val companies: Flow<List<Company>>
    get() = companyRepository.companies
      .onStart { companiesLceState.value = LceState.Loading }
      .onEach { companiesLceState.value = LceState.Content }
      .catch { e ->
        e.printStackTrace()
        companiesLceState.value = LceState.Error(e.message)
      }

  private val companiesLceState = MutableStateFlow<LceState>(LceState.None)
  override val companiesLce: Flow<LceState> = companiesLceState.asStateFlow()
}