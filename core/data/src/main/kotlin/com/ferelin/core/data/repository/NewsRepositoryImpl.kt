package com.ferelin.core.data.repository

import com.ferelin.core.data.api.STOCKS_TOKEN
import com.ferelin.core.data.entity.news.NewsApi
import com.ferelin.core.data.entity.news.NewsApiSpecifications
import com.ferelin.core.data.entity.news.NewsDao
import com.ferelin.core.data.mapper.NewsMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.repository.NewsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Named

internal class NewsRepositoryImpl @Inject constructor(
  private val api: NewsApi,
  private val dao: NewsDao,
  @Named(STOCKS_TOKEN) private val token: String
) : NewsRepository {
  override fun getAllBy(companyId: CompanyId): Observable<List<News>> {
    return dao.getAllBy(companyId.value)
      .distinctUntilChanged()
      .map { it.map(NewsMapper::map) }
  }

  override fun fetchNews(companyId: CompanyId, companyTicker: String): Completable {
    return try {
      val response = api
        .load(token, companyTicker)
        .blockingGet()
        .map(NewsApiSpecifications::convertToUnixTime)

      dao.eraseAllBy(companyId.value)
      dao.insertAll(NewsMapper.map(response, companyId))
      Completable.complete()
    } catch (e: Exception) {
      Completable.error(e)
    }
  }
}