package com.ferelin.stockprice.shared.commonMain.domain.entity

data class PastPrice(
  val id: PastPriceId,
  val companyId: CompanyId,
  val closePrice: Double,
  val dateMillis: Long
)

@JvmInline
value class PastPriceId(val value: Long)