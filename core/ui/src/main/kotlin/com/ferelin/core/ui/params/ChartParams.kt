package com.ferelin.core.ui.params

import com.ferelin.core.domain.entity.CompanyId

data class ChartParams(
  val companyId: CompanyId,
  val companyTicker: String
)