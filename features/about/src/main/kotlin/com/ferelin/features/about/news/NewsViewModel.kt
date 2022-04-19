package com.ferelin.features.about.news

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.CompanyNews
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.CompanyNewsUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class NewsUiState(
    val news: List<NewsViewData> = emptyList(),
    val newsLce: LceState = LceState.None,
    val newsFetchLce: LceState = LceState.None
)

internal class NewsViewModel(
    private val companyId: CompanyId,
    private val companyUseCase: CompanyUseCase,
    private val newsUseCase: CompanyNewsUseCase,
    private val dispatchersProvider: DispatchersProvider,
    networkListener: NetworkListener
) : ViewModel() {
    private val viewModelState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = viewModelState.asStateFlow()

    init {
        newsUseCase.getNewsBy(companyId)
            .toNewsViewData()
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onNews)
            .launchIn(viewModelScope)

        newsUseCase.newsLce
            .onEach(this::onNewsLce)
            .launchIn(viewModelScope)

        newsUseCase.newsFetchLce
            .onEach(this::onNewsFetchLce)
            .launchIn(viewModelScope)

        networkListener.networkState
            .filter { available -> available }
            .onEach { onNetworkAvailable() }
            .launchIn(viewModelScope)
    }

    fun fetchNews() {
        viewModelScope.launch(dispatchersProvider.IO) {
            val profile = companyUseCase.getBy(companyId).first()
            newsUseCase.fetchNews(
                companyId = companyId,
                companyTicker = profile.ticker
            )
        }
    }

    private fun onNetworkAvailable() {
        fetchNews()
    }

    private fun onNews(news: List<NewsViewData>) {
        viewModelState.update { it.copy(news = news) }
    }

    private fun onNewsLce(lceState: LceState) {
        viewModelState.update { it.copy(newsLce = lceState) }
    }

    private fun onNewsFetchLce(lceState: LceState) {
        viewModelState.update { it.copy(newsFetchLce = lceState) }
    }
}

private fun Flow<List<CompanyNews>>.toNewsViewData(): Flow<List<NewsViewData>> {
    return this.map { news ->
        news.map { it.toNewsViewData() }
    }
}