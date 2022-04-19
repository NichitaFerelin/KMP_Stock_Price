package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.companyNews.CompanyNewsApi
import com.ferelin.core.data.entity.companyNews.CompanyNewsDao
import com.ferelin.core.data.entity.companyNews.CompanyNewsRequestOptions
import com.ferelin.core.data.mapper.CompanyNewsMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.CompanyNews
import com.ferelin.core.domain.repository.CompanyNewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class CompanyNewsRepositoryImpl(
    private val api: CompanyNewsApi,
    private val dao: CompanyNewsDao,
    private val token: String
) : CompanyNewsRepository {
    override fun getAllBy(companyId: CompanyId): Flow<List<CompanyNews>> {
        return dao.getAllBy(companyId.value)
            .distinctUntilChanged()
            .map { it.map(CompanyNewsMapper::map) }
    }

    override suspend fun fetchNews(
        companyId: CompanyId,
        companyTicker: String
    ): Result<Any> = runCatching {
        val requestOptions = CompanyNewsRequestOptions(token, companyTicker)
        val response = api.load(requestOptions)
        val dbNews = CompanyNewsMapper.map(response, companyId)

        dao.eraseAllBy(companyId.value)
        dao.insertAll(dbNews)
    }
}