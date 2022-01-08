package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface NewsUseCase {
  fun getNewsBy(companyId: CompanyId): Flow<List<News>>
  suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
  val newsLce: Flow<LceState>
}

internal class NewsUseCaseImpl @Inject constructor(
  private val newsRepository: NewsRepository,
  private val dispatchersProvider: DispatchersProvider
) : NewsUseCase {
  override fun getNewsBy(companyId: CompanyId): Flow<List<News>> {
    return newsRepository.getAllBy(companyId)
      .onStart { newsLceState.value = LceState.Loading }
      .onEach { newsLceState.value = LceState.Content }
      .catch { e -> newsLceState.value = LceState.Error(e.message) }
      .flowOn(dispatchersProvider.IO)
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