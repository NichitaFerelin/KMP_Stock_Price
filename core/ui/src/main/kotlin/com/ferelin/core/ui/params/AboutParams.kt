package com.ferelin.core.ui.params

import com.ferelin.core.domain.entity.CompanyId

data class AboutParams(
  val companyId: CompanyId,
  val companyTicker: String,
  val companyName: String
)