package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.news.NewsApi
import com.ferelin.core.data.entity.news.NewsApiSpecifications
import com.ferelin.core.data.entity.news.NewsDao
import com.ferelin.core.data.entity.news.NewsRequestOptions
import com.ferelin.core.data.mapper.NewsMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*

internal class NewsRepositoryImpl(
  private val api: NewsApi,
  private val dao: NewsDao,
  private val token: String
) : NewsRepository {
  override fun getAllBy(companyId: CompanyId): Flow<List<News>> {
    return dao.getAllBy(companyId.value)
      .distinctUntilChanged()
      .map { it.map(NewsMapper::map) }
  }

  override suspend fun fetchNews(companyId: CompanyId, companyTicker: String) {
    try {
      val newsOptions = NewsRequestOptions(token, companyTicker)
      val response = api.load(newsOptions).map(NewsApiSpecifications::convertToUnixTime)

      dao.eraseAllBy(companyId.value)
      dao.insertAll(NewsMapper.map(response, companyId))
      fetchErrorState.value = null
    } catch (e: Exception) {
      fetchErrorState.value = e
    }
  }

  private val fetchErrorState = MutableStateFlow<Exception?>(null)
  override val fetchError: Flow<Exception?> = fetchErrorState.asStateFlow()
}