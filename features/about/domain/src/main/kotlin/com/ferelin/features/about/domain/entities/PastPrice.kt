package com.ferelin.features.about.domain.entities

import com.ferelin.core.domain.entities.entity.CompanyId

data class PastPrice(
  val id: PastPriceId,
  val companyId: CompanyId,
  val closePrice: Double,
  val dateMillis: Long
)

@JvmInline
value class PastPriceId(val value: Long)