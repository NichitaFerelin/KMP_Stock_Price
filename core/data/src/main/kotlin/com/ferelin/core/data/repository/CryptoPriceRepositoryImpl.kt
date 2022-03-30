package com.ferelin.core.data.repository

import com.ferelin.core.data.api.CRYPTOS_TOKEN
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.mapper.CryptoPriceMapper
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.repository.CryptoPriceRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Named

internal class CryptoPriceRepositoryImpl @Inject constructor(
  private val dao: CryptoPriceDao,
  private val api: CryptoPriceApi,
  @Named(CRYPTOS_TOKEN) private val token: String
) : CryptoPriceRepository {
  override val cryptoPrices: Observable<List<CryptoPrice>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(CryptoPriceMapper::map) }

  override fun fetchPriceFor(cryptos: List<Crypto>): Completable {
    return try {
      val cryptosContainer = cryptos.associateBy { it.ticker }
      val requestParam = cryptos.joinToString(separator = ",") { it.ticker }
      val cryptosDbo = api
        .load(token, requestParam)
        .blockingGet()
        .map { pojo -> CryptoPriceMapper.map(pojo, cryptosContainer[pojo.ticker]!!.id) }
      dao.insertAll(cryptosDbo)
      Completable.complete()
    } catch (e: Exception) {
      Completable.error(e)
    }
  }
}