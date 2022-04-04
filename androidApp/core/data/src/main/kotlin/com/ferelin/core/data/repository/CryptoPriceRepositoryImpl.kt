package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceOptions
import com.ferelin.core.data.mapper.CryptoPriceMapper
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.repository.CryptoPriceRepository
import kotlinx.coroutines.flow.*

internal class CryptoPriceRepositoryImpl(
  private val dao: CryptoPriceDao,
  private val api: CryptoPriceApi,
  private val token: String
) : CryptoPriceRepository {
  override val cryptoPrices: Flow<List<CryptoPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(CryptoPriceMapper::map) }

  override suspend fun fetchPriceFor(cryptos: List<Crypto>) {
    try {
      val cryptosContainer = cryptos.associateBy { it.ticker }
      val requestParam = cryptos.joinToString(separator = ",") { it.ticker }
      val options = CryptoPriceOptions(token, requestParam)
      val cryptosDbo = api.load(options).map { pojo ->
        CryptoPriceMapper.map(pojo, cryptosContainer[pojo.ticker]!!.id)
      }
      dao.insertAll(cryptosDbo)
      fetchErrorState.emit(null)
    } catch (e: Exception) {
      fetchErrorState.emit(e)
    }
  }

  private val fetchErrorState = MutableSharedFlow<Exception?>()
  override val fetchError: Flow<Exception?> = fetchErrorState.asSharedFlow()
}