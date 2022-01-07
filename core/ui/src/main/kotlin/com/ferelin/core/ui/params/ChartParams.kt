package com.ferelin.core.ui.params

import com.ferelin.core.domain.entities.entity.CompanyId

data class ChartParams(
  val companyId: CompanyId,
  val companyTicker: String
)