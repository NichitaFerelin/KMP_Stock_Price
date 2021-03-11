package com.ferelin.local.database

import com.ferelin.local.models.Company
import kotlinx.coroutines.flow.Flow

interface CompaniesManagerHelper {

    fun insertCompany(company: Company)

    fun insertAllCompanies(list: List<Company>)

    fun updateCompany(company: Company)

    fun getAllCompanies(): Flow<List<Company>>

    fun deleteCompany(company: Company)
}