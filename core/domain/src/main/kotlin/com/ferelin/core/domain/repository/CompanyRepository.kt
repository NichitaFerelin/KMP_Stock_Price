package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    val companies: Flow<List<Company>>
    val favouriteCompanies: Flow<List<Company>>
}