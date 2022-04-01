package com.ferelin.features.about.news

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.NewsUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.NewsParams
import kotlinx.coroutines.flow.*

@Immutable
internal data class NewsStateUi(
  val news: List<NewsViewData> = emptyList(),
  val newsLce: LceState = LceState.None,
  val showNetworkError: Boolean = false
)

internal class NewsViewModel(
  private val newsParams: NewsParams,
  private val newsUseCase: NewsUseCase,
  dispatchersProvider: DispatchersProvider,
  networkListener: NetworkListener
) : ViewModel() {
  private val viewModelState = MutableStateFlow(NewsStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    newsUseCase.getNewsBy(newsParams.companyId)
      .map { it.map(NewsMapper::map) }
      .onEach(this::onNews)
      .launchIn(viewModelScope)

    newsUseCase.newsLce
      .onEach(this::onNewsLce)
      .launchIn(viewModelScope)

    networkListener.networkState
      .distinctUntilChanged()
      .onEach(this::onNetwork)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  private fun onNews(news: List<NewsViewData>) {
    viewModelState.update { it.copy(news = news) }
  }

  private fun onNewsLce(lceState: LceState) {
    viewModelState.update { it.copy(newsLce = lceState) }
  }

  private suspend fun onNetwork(available: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !available) }
    if (available) newsUseCase.fetchNews(newsParams.companyId, newsParams.companyTicker)
  }
}