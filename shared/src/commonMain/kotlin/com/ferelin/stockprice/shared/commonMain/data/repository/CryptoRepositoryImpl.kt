package com.ferelin.stockprice.shared.commonMain.data.repository

import com.ferelin.common.domain.repository.CryptoRepository
import com.ferelin.stockprice.shared.commonMain.data.entity.crypto.CryptoDao
import com.ferelin.stockprice.shared.commonMain.data.entity.crypto.CryptoJsonSource
import com.ferelin.stockprice.shared.commonMain.data.mapper.CryptoMapper
import com.ferelin.stockprice.shared.commonMain.domain.entity.Crypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CryptoRepositoryImpl(
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