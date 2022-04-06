package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.db.CryptoDBO
import com.ferelin.stockprice.shared.data.entity.crypto.CryptoJson
import com.ferelin.stockprice.shared.domain.entity.Crypto
import com.ferelin.stockprice.shared.domain.entity.CryptoId

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
        logoUrl = cryptoJson.logoUrl
      )
    }
  }
}