package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.pastPrice.PastPriceApi
import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.pastPrice.PastPricesOptions
import com.ferelin.core.data.mapper.PastPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class PastPriceRepositoryImpl(
    private val api: PastPriceApi,
    private val dao: PastPriceDao,
    private val token: String
) : PastPriceRepository {
    override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
        return dao.getAllBy(companyId.value)
            .distinctUntilChanged()
            .map { it.map(PastPriceMapper::map) }
    }

    override suspend fun fetchPastPrices(
        companyId: CompanyId,
        companyTicker: String
    ): Result<Any> = runCatching {
        val requestOptions = PastPricesOptions(token, companyTicker)
        val response = api.load(requestOptions)
        val dbPastPrices = PastPriceMapper.map(response, companyId)

        dao.eraseAllBy(companyId.value)
        dao.insertAll(dbPastPrices)
    }
}