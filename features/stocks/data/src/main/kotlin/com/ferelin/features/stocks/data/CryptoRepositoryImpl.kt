package com.ferelin.features.stocks.data

import com.ferelin.features.stocks.data.entity.crypto.CryptoDao
import com.ferelin.features.stocks.data.entity.crypto.CryptoJsonSource
import com.ferelin.features.stocks.data.mapper.CryptoMapper
import com.ferelin.features.stocks.domain.entity.Crypto
import com.ferelin.features.stocks.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class CryptoRepositoryImpl @Inject constructor(
  private val dao: CryptoDao,
  private val jsonSource: CryptoJsonSource
) : CryptoRepository {
  override val cryptos: Flow<List<Crypto>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(CryptoMapper::map) }
      .onEach { dbCryptos ->
        if (dbCryptos.isEmpty()) {
          dao.insertAll(
            cryptosDBO = jsonSource.parseJson()
          )
        }
      }
}