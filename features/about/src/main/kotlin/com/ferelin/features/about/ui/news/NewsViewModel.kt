package com.ferelin.features.about.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.NewsUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.LazyThreadSafetyMode.NONE

internal class NewsViewModel(
  private val newsParams: NewsParams,
  dispatchersProvider: DispatchersProvider,
  newsUseCase: NewsUseCase,
  networkListener: NetworkListener
) : ViewModel() {
  val networkState = networkListener.networkState
    .distinctUntilChanged()
    .onEach { isNetworkAvailable ->
      if (isNetworkAvailable) {
        newsUseCase.fetchNews(newsParams.companyId, newsParams.companyTicker)
      }
    }
    .flowOn(dispatchersProvider.IO)

  val newsLce = newsUseCase.newsLce
  val news = newsUseCase.getNewsBy(newsParams.companyId)
    .map { it.map(NewsMapper::map) }

  val newsAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(createNewsAdapter(this::onNewsItemClick))
      .apply { setHasStableIds(true) }
  }

  private fun onNewsItemClick(newsViewData: NewsViewData) {
    // open url
  }
}

internal class NewsViewModelFactory @AssistedInject constructor(
  @Assisted(NEWS_PARAMS) private val newsParams: NewsParams,
  private val newsUseCase: NewsUseCase,
  private val networkListener: NetworkListener,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == NewsViewModel::class.java)
    return NewsViewModel(newsParams, dispatchersProvider, newsUseCase, networkListener) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(NEWS_PARAMS) newsParams: NewsParams): NewsViewModelFactory
  }
}

internal const val NEWS_PARAMS = "news-params"