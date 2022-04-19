package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.CompanyNews
import kotlinx.coroutines.flow.Flow

interface CompanyNewsRepository {
    fun getAllBy(companyId: CompanyId): Flow<List<CompanyNews>>
    suspend fun fetchNews(
        companyId: CompanyId,
        companyTicker: String
    ): Result<Any>
}