package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.mapper.CryptoMapper
import com.ferelin.core.domain.repository.CryptoRepository
import com.ferelin.core.domain.entity.Crypto
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