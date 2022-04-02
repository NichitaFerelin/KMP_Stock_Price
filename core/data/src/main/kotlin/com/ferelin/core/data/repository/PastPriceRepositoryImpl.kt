package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.pastPrice.PastPricesOptions
import com.ferelin.core.data.mapper.PastPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.*

internal class PastPriceRepositoryImpl(
  private val api: PastPricesApi,
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
      val requestOptions = PastPricesOptions(token, companyTicker)
      val response = api.load(requestOptions)
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