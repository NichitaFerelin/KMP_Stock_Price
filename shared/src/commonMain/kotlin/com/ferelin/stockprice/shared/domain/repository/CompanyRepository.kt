package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
  val companies: Flow<List<Company>>
}