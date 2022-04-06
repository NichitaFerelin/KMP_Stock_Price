package com.ferelin.common.domain.usecase

import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.LceState
import com.ferelin.stockprice.shared.commonMain.domain.entity.News
import com.ferelin.common.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*

interface NewsUseCase {
  fun getNewsBy(companyId: CompanyId): Flow<List<News>>
  suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
  val newsLce: Flow<LceState>
}

internal class NewsUseCaseImpl(
  private val newsRepository: NewsRepository
) : NewsUseCase {
  override fun getNewsBy(companyId: CompanyId): Flow<List<News>> {
    return newsRepository.getAllBy(companyId)
      .onStart { newsLceState.value = LceState.Loading }
      .onEach { newsLceState.value = LceState.Content }
      .catch { e -> newsLceState.value = LceState.Error(e.message) }
  }

  override suspend fun fetchNews(companyId: CompanyId, companyTicker: String) {
    try {
      newsLceState.value = LceState.Loading
      newsRepository.fetchNews(companyId, companyTicker)
      newsLceState.value = LceState.Content
    } catch (e: Exception) {
      newsLceState.value = LceState.Error(e.message)
    }
  }

  private val newsLceState = MutableStateFlow<LceState>(LceState.None)
  override val newsLce: Flow<LceState> = newsLceState.asStateFlow()
}