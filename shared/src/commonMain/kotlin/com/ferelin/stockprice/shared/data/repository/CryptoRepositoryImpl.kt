package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.shared.data.entity.crypto.CryptoApi
import com.ferelin.stockprice.shared.data.entity.crypto.CryptoDao
import com.ferelin.stockprice.shared.data.mapper.CryptoMapper
import com.ferelin.stockprice.shared.domain.entity.Crypto
import com.ferelin.stockprice.shared.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class CryptoRepositoryImpl(
    private val dao: CryptoDao,
    private val api: CryptoApi
) : CryptoRepository {
    override val cryptos: Flow<List<Crypto>>
        get() = dao.getAll()
            .distinctUntilChanged()
            .map { it.map(CryptoMapper::map) }
            .onEach { dbCryptos ->
                if (dbCryptos.isEmpty()) {
                    val apiCryptos = api.load()
                    dao.insertAll(cryptosDBO = CryptoMapper.map(apiCryptos))
                }
            }
}