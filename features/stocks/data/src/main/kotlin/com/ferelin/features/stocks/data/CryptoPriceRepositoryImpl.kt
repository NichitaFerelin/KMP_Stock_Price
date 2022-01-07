package com.ferelin.features.stocks.data

import com.ferelin.features.stocks.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.features.stocks.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.features.stocks.data.mapper.CryptoPriceMapper
import com.ferelin.features.stocks.domain.entity.Crypto
import com.ferelin.features.stocks.domain.entity.CryptoPrice
import com.ferelin.features.stocks.domain.repository.CryptoPriceRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class CryptoPriceRepositoryImpl @Inject constructor(
  private val dao: CryptoPriceDao,
  private val api: CryptoPriceApi
) : CryptoPriceRepository {
  override val cryptoPrices: Flow<List<CryptoPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(CryptoPriceMapper::map) }

  override suspend fun fetchPriceFor(cryptos: List<Crypto>) {
    try {
      val cryptosContainer = cryptos.associateBy { it.ticker }
      val requestParam = cryptos.joinToString(separator = ",") { it.ticker }
      val cryptosDbo = api.load(requestParam).data.map { pojo ->
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