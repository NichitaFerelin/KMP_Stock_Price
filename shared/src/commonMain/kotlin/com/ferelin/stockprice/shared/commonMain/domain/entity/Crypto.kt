package com.ferelin.stockprice.shared.commonMain.domain.entity

data class Crypto(
  val id: CryptoId,
  val ticker: String,
  val name: String,
  val logoUrl: String
)

data class CryptoPrice(
  val cryptoId: CryptoId,
  val price: Double,
  val priceChange: Double,
  val priceChangePercents: Double
)

@JvmInline
value class CryptoId(val value: Int)