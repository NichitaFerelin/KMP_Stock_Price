package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
  fun getAllBy(companyId: CompanyId): Flow<List<News>>
  suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}