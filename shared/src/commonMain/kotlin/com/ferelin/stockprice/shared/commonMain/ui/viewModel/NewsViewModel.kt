package com.ferelin.stockprice.shared.commonMain.ui.viewModel

import com.ferelin.common.domain.usecase.NewsUseCase
import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.LceState
import com.ferelin.stockprice.shared.commonMain.ui.DispatchersProvider
import com.ferelin.stockprice.shared.commonMain.ui.mapper.NewsMapper
import com.ferelin.stockprice.shared.commonMain.ui.params.NewsParams
import com.ferelin.stockprice.shared.commonMain.ui.viewData.NewsViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

data class NewsStateUi(
  val news: List<NewsViewData> = emptyList(),
  val newsLce: LceState = LceState.None,
  val showNetworkError: Boolean = false
)

class NewsViewModel(
  private val newsParams: NewsParams,
  private val newsUseCase: NewsUseCase,
  viewModelScope: CoroutineScope,
  dispatchersProvider: DispatchersProvider
) {
  private val viewModelState = MutableStateFlow(NewsStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    newsUseCase.getNewsBy(companyId = CompanyId(newsParams.companyId))
      .map { it.map(NewsMapper::map) }
      .onEach(this::onNews)
      .launchIn(viewModelScope)

    newsUseCase.newsLce
      .onEach(this::onNewsLce)
      .launchIn(viewModelScope)

    /*networkListener.networkState
      .distinctUntilChanged()
      .onEach(this::onNetwork)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)*/
  }

  private fun onNews(news: List<NewsViewData>) {
    viewModelState.update { it.copy(news = news) }
  }

  private fun onNewsLce(lceState: LceState) {
    viewModelState.update { it.copy(newsLce = lceState) }
  }

  private suspend fun onNetwork(available: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !available) }
    if (available) {
      val companyId = CompanyId(newsParams.companyId)
      newsUseCase.fetchNews(companyId, newsParams.companyTicker)
    }
  }
}