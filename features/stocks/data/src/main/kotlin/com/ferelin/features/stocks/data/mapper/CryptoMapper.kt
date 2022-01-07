package com.ferelin.features.stocks.data.mapper

import com.ferelin.features.stocks.data.entity.crypto.CryptoDBO
import com.ferelin.features.stocks.data.entity.crypto.CryptoJson
import com.ferelin.features.stocks.domain.entity.Crypto
import com.ferelin.features.stocks.domain.entity.CryptoId

internal object CryptoMapper {
  fun map(crypto: Crypto): CryptoDBO {
    return CryptoDBO(
      id = crypto.id.value,
      ticker = crypto.ticker,
      name = crypto.name,
      logoUrl = crypto.logoUrl
    )
  }

  fun map(cryptoDBO: CryptoDBO): Crypto {
    return Crypto(
      id = CryptoId(cryptoDBO.id),
      ticker = cryptoDBO.ticker,
      name = cryptoDBO.name,
      logoUrl = cryptoDBO.logoUrl
    )
  }

  fun map(cryptosJson: List<CryptoJson>): List<CryptoDBO> {
    return cryptosJson.mapIndexed { index, cryptoJson ->
      CryptoDBO(
        id = index,
        ticker = cryptoJson.symbol,
        name = cryptoJson.name,
        logoUrl = cryptoJson.logo_url
      )
    }
  }
}