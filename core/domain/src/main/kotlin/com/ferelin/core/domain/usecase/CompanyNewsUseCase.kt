package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.CompanyNews
import com.ferelin.core.domain.repository.CompanyNewsRepository
import kotlinx.coroutines.flow.*

interface CompanyNewsUseCase {
    val newsLce: Flow<LceState>
    val newsFetchLce: Flow<LceState>
    fun getNewsBy(companyId: CompanyId): Flow<List<CompanyNews>>
    suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
}

internal class CompanyNewsUseCaseImpl(
    private val newsRepository: CompanyNewsRepository,
    private val dispatchersProvider: DispatchersProvider
) : CompanyNewsUseCase {
    override fun getNewsBy(companyId: CompanyId): Flow<List<CompanyNews>> {
        return newsRepository.getAllBy(companyId)
            .onStart { newsLceState.value = LceState.Loading }
            .onEach { newsLceState.value = LceState.Content }
            .catch { e -> newsLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)
    }

    private val newsLceState = MutableStateFlow<LceState>(LceState.None)
    override val newsLce: Flow<LceState> = newsLceState.asStateFlow()

    override suspend fun fetchNews(companyId: CompanyId, companyTicker: String) {
        newsFetchLceState.value = LceState.Loading
        newsRepository.fetchNews(companyId, companyTicker)
            .onSuccess { newsFetchLceState.value = LceState.Content }
            .onFailure { newsFetchLceState.value = LceState.Error(it.message) }
    }

    private val newsFetchLceState = MutableStateFlow<LceState>(LceState.None)
    override val newsFetchLce: Flow<LceState> = newsFetchLceState.asStateFlow()
}