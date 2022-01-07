package com.ferelin.features.about.data

import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.features.about.data.entity.pastPrice.PastPriceDao
import com.ferelin.features.about.data.entity.pastPrice.PastPricesApi
import com.ferelin.features.about.data.mapper.PastPriceMapper
import com.ferelin.features.about.domain.entities.PastPrice
import com.ferelin.features.about.domain.repositories.PastPricesRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PastPriceRepositoryImpl @Inject constructor(
  private val api: PastPricesApi,
  private val dao: PastPriceDao
) : PastPricesRepository {
  override fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>> {
    return dao.getAllBy(companyId.value)
      .distinctUntilChanged()
      .map { it.map(PastPriceMapper::map) }
  }

  override suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String) {
    checkBackgroundThread()
    try {
      val response = api.load(companyTicker)
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