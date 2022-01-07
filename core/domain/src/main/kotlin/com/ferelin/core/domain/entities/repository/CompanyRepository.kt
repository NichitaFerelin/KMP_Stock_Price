package com.ferelin.core.domain.entities.repository

import com.ferelin.core.domain.entities.entity.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
  val companies: Flow<List<Company>>
}