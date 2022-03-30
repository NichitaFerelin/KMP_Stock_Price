package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.repository.NewsRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface NewsUseCase {
  fun getNewsBy(companyId: CompanyId): Observable<List<News>>
  fun fetchNews(companyId: CompanyId, companyTicker: String)
  val newsLce: Flow<LceState>
}

@Reusable
internal class NewsUseCaseImpl @Inject constructor(
  private val newsRepository: NewsRepository
) : NewsUseCase {
  override fun getNewsBy(companyId: CompanyId): Observable<List<News>> {
    return newsRepository.getAllBy(companyId)
      .doOnSubscribe { newsLceState.value = LceState.Loading }
      .doOnEach { newsLceState.value = LceState.Content }
      .doOnError { e -> newsLceState.value = LceState.Error(e.message) }
  }

  override fun fetchNews(companyId: CompanyId, companyTicker: String) {
    newsRepository.fetchNews(companyId, companyTicker)
      .doOnSubscribe { newsLceState.value = LceState.Loading }
      .doOnComplete { newsLceState.value = LceState.Content }
      .doOnError { newsLceState.value = LceState.Error(it.message) }
      .blockingAwait()
  }

  private val newsLceState = MutableStateFlow<LceState>(LceState.None)
  override val newsLce: Flow<LceState> = newsLceState.asStateFlow()
}