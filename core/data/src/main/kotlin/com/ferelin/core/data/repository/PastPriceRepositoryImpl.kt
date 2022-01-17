package com.ferelin.core.data.repository

import com.ferelin.core.data.api.STOCKS_TOKEN
import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.pastPrice.PastPricesApi
import com.ferelin.core.data.mapper.PastPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.domain.repository.PastPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

internal class PastPriceRepositoryImpl @Inject constructor(
  private val api: PastPricesApi,
  private val dao: PastPriceDao,
  @Named(STOCKS_TOKEN) private val token: String
) : PastPriceRepository {
  override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
    return dao.getAllBy(companyId.value)
      .distinctUntilChanged()
      .map { it.map(PastPriceMapper::map) }
  }

  override suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String) {
    try {
      val response = api.load(token, companyTicker)
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