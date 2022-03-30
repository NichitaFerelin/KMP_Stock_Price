package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.mapper.CryptoMapper
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.repository.CryptoRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class CryptoRepositoryImpl @Inject constructor(
  private val dao: CryptoDao,
  private val jsonSource: CryptoJsonSource
) : CryptoRepository {
  override val cryptos: Observable<List<Crypto>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(CryptoMapper::map) }
      .doOnEach { dbCryptosNotification ->
        val dbCryptos = dbCryptosNotification.value ?: emptyList()
        if (dbCryptos.isEmpty()) {
          dao.insertAll(cryptosDBO = jsonSource.parseJson())
        }
      }
}