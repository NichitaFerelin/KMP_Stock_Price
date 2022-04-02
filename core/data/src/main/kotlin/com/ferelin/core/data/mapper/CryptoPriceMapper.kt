package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.cryptoPrice.CryptoPricePojo
import com.ferelin.core.domain.entity.CryptoId
import com.ferelin.core.domain.entity.CryptoPrice
import stockprice.CryptoPriceDBO

internal object CryptoPriceMapper {
  fun map(cryptoPrice: CryptoPrice): CryptoPriceDBO {
    return CryptoPriceDBO(
      id = cryptoPrice.cryptoId.value,
      price = cryptoPrice.price,
      priceChange = cryptoPrice.priceChange,
      priceChangePercents = cryptoPrice.priceChangePercents
    )
  }

  fun map(cryptoPriceDBO: CryptoPriceDBO): CryptoPrice {
    return CryptoPrice(
      cryptoId = CryptoId(cryptoPriceDBO.id),
      price = cryptoPriceDBO.price,
      priceChange = cryptoPriceDBO.priceChange,
      priceChangePercents = cryptoPriceDBO.priceChangePercents
    )
  }

  fun map(
    cryptoPricePojo: CryptoPricePojo,
    ownerId: CryptoId
  ): CryptoPriceDBO {
    return CryptoPriceDBO(
      id = ownerId.value,
      price = cryptoPricePojo.price.toDouble(),
      priceChange = cryptoPricePojo.priceChangeInfo.value.toDouble(),
      priceChangePercents = cryptoPricePojo.priceChangeInfo.percents.toDouble()
    )
  }
}