package com.ferelin.features.about.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.NewsUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.NewsParams
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

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
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map { it.map(NewsMapper::map) }
      .subscribe(this::onNews) { Timber.e(it) }

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

  private fun onNetwork(available: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !available) }
    if (available) newsUseCase.fetchNews(newsParams.companyId, newsParams.companyTicker)
  }
}

internal class NewsViewModelFactory @Inject constructor(
  private val newsParams: NewsParams,
  private val newsUseCase: NewsUseCase,
  private val networkListener: NetworkListener,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == NewsViewModel::class.java)
    return NewsViewModel(newsParams, newsUseCase, dispatchersProvider, networkListener) as T
  }
}