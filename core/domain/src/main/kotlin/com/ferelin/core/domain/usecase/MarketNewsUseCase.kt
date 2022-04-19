package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.MarketNews
import com.ferelin.core.domain.repository.MarketNewsRepository
import kotlinx.coroutines.flow.*

interface MarketNewsUseCase {
    val marketNews: Flow<List<MarketNews>>
    val marketNewsLce: Flow<LceState>
    val marketNewsFetchLce: Flow<LceState>
    suspend fun fetchNews()
}

internal class MarketNewsUseCaseImpl(
    private val marketNewsRepository: MarketNewsRepository,
    private val dispatchersProvider: DispatchersProvider
) : MarketNewsUseCase {

    override val marketNews: Flow<List<MarketNews>>
        get() = marketNewsRepository.marketNews
            .onStart { marketNewsLceState.value = LceState.Loading }
            .onEach { marketNewsLceState.value = LceState.Content }
            .catch { e -> marketNewsLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)


    private val marketNewsLceState = MutableStateFlow<LceState>(LceState.None)
    override val marketNewsLce: Flow<LceState> = marketNewsLceState.asStateFlow()

    override suspend fun fetchNews() {
        marketNewsFetchLceState.value = LceState.Loading
        marketNewsRepository.fetchMarketNews()
            .onSuccess { marketNewsFetchLceState.value = LceState.Content }
            .onFailure { marketNewsFetchLceState.value = LceState.Error(it.message) }
    }

    private val marketNewsFetchLceState = MutableStateFlow<LceState>(LceState.None)
    override val marketNewsFetchLce: Flow<LceState> = marketNewsFetchLceState.asStateFlow()
}