package com.ferelin.features.about.data

import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.features.about.data.entity.news.NewsApi
import com.ferelin.features.about.data.entity.news.NewsDao
import com.ferelin.features.about.data.mapper.NewsMapper
import com.ferelin.features.about.domain.entities.News
import com.ferelin.features.about.domain.repositories.NewsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class NewsRepositoryImpl @Inject constructor(
  private val api: NewsApi,
  private val dao: NewsDao
) : NewsRepository {
  override fun getAllBy(companyId: CompanyId): Flow<List<News>> {
    return dao.getAllBy(companyId.value)
      .distinctUntilChanged()
      .map { it.map(NewsMapper::map) }
  }

  override suspend fun fetchNews(companyId: CompanyId, companyTicker: String) {
    checkBackgroundThread()
    try {
      val response = api.load(companyTicker)
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