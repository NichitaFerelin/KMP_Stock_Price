package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
  fun getAllBy(companyId: CompanyId): Flow<List<News>>
  suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}