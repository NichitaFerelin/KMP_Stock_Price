package com.ferelin.common.domain.repository

import com.ferelin.stockprice.shared.commonMain.domain.entity.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
  val companies: Flow<List<Company>>
}