package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.db.CryptoDBO
import com.ferelin.stockprice.shared.data.entity.crypto.CryptoPojo
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

  fun map(cryptosJson: List<CryptoPojo>): List<CryptoDBO> {
    return cryptosJson.mapIndexed { index, pojo ->
      CryptoDBO(
        id = index,
        ticker = pojo.symbol,
        name = pojo.name,
        logoUrl = pojo.logoUrl
      )
    }
  }
}