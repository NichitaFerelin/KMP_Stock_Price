package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.usecase.NewsUseCase
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.mapper.NewsMapper
import com.ferelin.stockprice.shared.ui.params.NewsParams
import com.ferelin.stockprice.shared.ui.viewData.NewsViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NewsStateUi internal constructor(
    val news: List<NewsViewData> = emptyList(),
    val newsLce: LceState = LceState.None,
)

class NewsViewModel internal constructor(
    private val newsParams: NewsParams,
    private val newsUseCase: NewsUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatchersProvider: DispatchersProvider
) {
    private val viewModelState = MutableStateFlow(NewsStateUi())
    val uiState = viewModelState.asStateFlow()

    init {
        newsUseCase
            .getNewsBy(companyId = CompanyId(newsParams.companyId))
            .map { it.map(NewsMapper::map) }
            .onEach(this::onNews)
            .launchIn(viewModelScope)

        newsUseCase.newsLce
            .onEach(this::onNewsLce)
            .launchIn(viewModelScope)
    }

    private fun onNews(news: List<NewsViewData>) {
        viewModelState.update { it.copy(news = news) }
        fetchNews()
    }

    private fun onNewsLce(lceState: LceState) {
        viewModelState.update { it.copy(newsLce = lceState) }
    }

    private fun fetchNews() {
        viewModelScope.launch(dispatchersProvider.IO) {
            val companyId = CompanyId(newsParams.companyId)
            newsUseCase.fetchNews(companyId, newsParams.companyTicker)
        }
    }
}