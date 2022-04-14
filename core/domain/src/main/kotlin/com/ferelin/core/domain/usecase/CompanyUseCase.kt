package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.*

interface CompanyUseCase {
    val companies: Flow<List<Company>>
    val companiesLce: StateFlow<LceState>
    val favouriteCompanies: Flow<List<Company>>
    val favouriteCompaniesLce: StateFlow<LceState>
}

internal class CompanyUseCaseImpl(
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
    override val companiesLce: StateFlow<LceState> = companiesLceState.asStateFlow()

    override val favouriteCompanies: Flow<List<Company>>
        get() = companyRepository.favouriteCompanies
            .onStart { favouriteCompaniesLceState.value = LceState.Loading }
            .onEach { favouriteCompaniesLceState.value = LceState.Content }
            .catch { e -> favouriteCompaniesLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)

    private val favouriteCompaniesLceState = MutableStateFlow<LceState>(LceState.None)
    override val favouriteCompaniesLce: StateFlow<LceState> =
        favouriteCompaniesLceState.asStateFlow()
}