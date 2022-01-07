package com.ferelin.features.about.domain.repositories

import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.features.about.domain.entities.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
  fun getAllBy(companyId: CompanyId): Flow<List<News>>
  suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}