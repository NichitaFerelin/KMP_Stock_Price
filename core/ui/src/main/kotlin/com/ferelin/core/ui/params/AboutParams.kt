package com.ferelin.core.ui.params

import com.ferelin.core.domain.entities.entity.CompanyId

data class AboutParams(
  val companyId: CompanyId,
  val companyTicker: String,
  val companyName: String
)