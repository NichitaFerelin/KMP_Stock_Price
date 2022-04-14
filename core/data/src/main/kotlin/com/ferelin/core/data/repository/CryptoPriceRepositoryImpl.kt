package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceOptions
import com.ferelin.core.data.entity.cryptoPrice.CryptoPricePojo
import com.ferelin.core.data.mapper.CryptoPriceMapper
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoId
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.repository.CryptoPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class CryptoPriceRepositoryImpl(
    private val dao: CryptoPriceDao,
    private val api: CryptoPriceApi,
    private val token: String
) : CryptoPriceRepository {
    override val cryptoPrices: Flow<List<CryptoPrice>>
        get() = dao.getAll()
            .distinctUntilChanged()
            .map { it.map(CryptoPriceMapper::map) }

    override suspend fun fetchPriceFor(
        cryptos: List<Crypto>
    ): Result<Any> = runCatching {
        val requestOptions = cryptos.toCryptoPriceOptions(token)
        val response = api.load(requestOptions)
        val associatedCryptosResponse = response.associateWithOwnerId(cryptos)
        val dbCryptos = CryptoPriceMapper.map(associatedCryptosResponse)

        dao.insertAll(dbCryptos)
    }
}

private fun List<Crypto>.toCryptoPriceOptions(
    token: String
): CryptoPriceOptions {
    val requestParam = this.joinToString(separator = ",") { it.ticker }
    return CryptoPriceOptions(token, requestParam)
}

private fun List<CryptoPricePojo>.associateWithOwnerId(
    cryptos: List<Crypto>
): Map<CryptoPricePojo, CryptoId> {
    val cryptosContainer = cryptos.associateBy { it.ticker }
    return this.associateWith { cryptosContainer[it.ticker]!!.id }
}