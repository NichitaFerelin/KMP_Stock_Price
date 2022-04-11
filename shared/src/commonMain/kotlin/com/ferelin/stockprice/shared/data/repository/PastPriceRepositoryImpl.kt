package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.shared.data.entity.pastPrice.PastPriceApi
import com.ferelin.stockprice.shared.data.entity.pastPrice.PastPriceDao
import com.ferelin.stockprice.shared.data.entity.pastPrice.PastPricesOptions
import com.ferelin.stockprice.shared.data.mapper.PastPriceMapper
import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.PastPrice
import com.ferelin.stockprice.shared.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.*

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

    override suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String) {
        try {
            val options = PastPricesOptions(token, companyTicker)
            val response = api.load(options)
            dao.eraseAllBy(companyId.value)
            dao.insertAll(PastPriceMapper.map(response, companyId))
            fetchErrorState.value = null
        } catch (e: Exception) {
            fetchErrorState.value = e
        }
    }

    private val fetchErrorState = MutableStateFlow<Exception?>(null)
    override val fetchError: Flow<Exception?> = fetchErrorState.asStateFlow()
}