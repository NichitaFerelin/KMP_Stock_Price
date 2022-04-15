package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.FavoriteCompany
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.*

interface CompanyUseCase {
    val companies: Flow<List<Company>>
    val companiesLce: StateFlow<LceState>
    val favoriteCompanies: Flow<List<FavoriteCompany>>
    val favoriteCompaniesLce: StateFlow<LceState>
    suspend fun addToFavorites(id: CompanyId)
    suspend fun eraseFromFavorites(id: CompanyId)
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

    override val favoriteCompanies: Flow<List<FavoriteCompany>>
        get() = companyRepository.favoriteCompanies
            .onStart { favoriteCompaniesLceState.value = LceState.Loading }
            .onEach { favoriteCompaniesLceState.value = LceState.Content }
            .catch { e -> favoriteCompaniesLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)

    private val favoriteCompaniesLceState = MutableStateFlow<LceState>(LceState.None)
    override val favoriteCompaniesLce: StateFlow<LceState> =
        favoriteCompaniesLceState.asStateFlow()

    override suspend fun addToFavorites(id: CompanyId) {
        companyRepository.addToFavorites(id)
    }

    override suspend fun eraseFromFavorites(id: CompanyId) {
        companyRepository.eraseFromFavorites(id)
    }
}