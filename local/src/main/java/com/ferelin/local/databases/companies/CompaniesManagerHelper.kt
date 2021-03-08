package com.ferelin.local.databases.companies

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface CompaniesManagerHelper {

    fun insertCompany(company: Company)

    fun insertAllCompanies(list: List<Company>)

    fun updateCompany(company: Company)

    fun getAllCompanies(): Flow<List<Company>>

    fun getCompany(symbol: String): Flow<Company>

    fun deleteCompany(symbol: String)

    fun deleteCompany(company: Company)
}