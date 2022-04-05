package com.ferelin.stockprice.data.repository

import com.ferelin.stockprice.data.entity.news.NewsApi
import com.ferelin.stockprice.data.entity.news.NewsDao
import com.ferelin.stockprice.data.entity.news.NewsRequestOptions
import com.ferelin.stockprice.data.mapper.NewsMapper
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.News
import com.ferelin.common.domain.repository.NewsRepository
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
      val options = NewsRequestOptions(token, companyTicker)
      val response = api.load(options)
        // .map(NewsApiSpecifications::convertToUnixTime)

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