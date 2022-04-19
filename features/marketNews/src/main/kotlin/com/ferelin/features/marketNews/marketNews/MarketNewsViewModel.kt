package com.ferelin.features.marketNews.marketNews

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.MarketNews
import com.ferelin.core.domain.usecase.MarketNewsUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class MarketNewsUiState(
    val marketNews: List<MarketNewsViewData> = emptyList(),
    val marketNewsLce: LceState = LceState.None,
    val marketNewsFetchLce: LceState = LceState.None
)

internal class MarketNewsViewModel(
    private val marketNewsUseCase: MarketNewsUseCase,
    private val dispatchersProvider: DispatchersProvider,
    networkListener: NetworkListener
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MarketNewsUiState())
    val uiState: StateFlow<MarketNewsUiState> = viewModelState.asStateFlow()

    init {
        marketNewsUseCase.marketNews
            .map { it.reversed() }
            .toMarketNewsViewData()
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onMarketNews)
            .launchIn(viewModelScope)

        marketNewsUseCase.marketNewsLce
            .onEach(this::onMarketNewsLce)
            .launchIn(viewModelScope)

        marketNewsUseCase.marketNewsFetchLce
            .onEach(this::onMarketNewsFetchLce)
            .launchIn(viewModelScope)

        networkListener.networkState
            .filter { available -> available }
            .onEach { onNetworkAvailable() }
            .launchIn(viewModelScope)
    }

    fun fetchNews() {
        viewModelScope.launch(dispatchersProvider.IO) {
            marketNewsUseCase.fetchNews()
        }
    }

    private fun onNetworkAvailable() {
        fetchNews()
    }

    private fun onMarketNews(marketNews: List<MarketNewsViewData>) {
        viewModelState.update { it.copy(marketNews = marketNews) }
    }

    private fun onMarketNewsLce(lceState: LceState) {
        viewModelState.update { it.copy(marketNewsLce = lceState) }
    }

    private fun onMarketNewsFetchLce(lceState: LceState) {
        viewModelState.update { it.copy(marketNewsFetchLce = lceState) }
    }
}

private fun Flow<List<MarketNews>>.toMarketNewsViewData(): Flow<List<MarketNewsViewData>> {
    return this.map { marketNews ->
        marketNews.map { it.toMarketNewsViewData() }
    }
}