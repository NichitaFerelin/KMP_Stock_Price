package com.ferelin.features.about.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.features.about.domain.NewsUseCase
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class NewsViewModel @Inject constructor(
  private val newsParams: NewsParams,
  private val router: Router,
  newsUseCase: NewsUseCase,
  networkListener: NetworkListener
) : ViewModel() {

  val networkState = networkListener.networkState
    .distinctUntilChanged()
    .onEach { newsUseCase.fetchNews(newsParams.companyId, newsParams.companyTicker) }

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

internal class NewsViewModelFactory @Inject constructor(
  var newsParams: NewsParams? = null,
  private val router: Router,
  private val newsUseCase: NewsUseCase,
  private val networkListener: NetworkListener
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return NewsViewModel(
      newsParams!!,
      router,
      newsUseCase,
      networkListener
    ) as T
  }
}