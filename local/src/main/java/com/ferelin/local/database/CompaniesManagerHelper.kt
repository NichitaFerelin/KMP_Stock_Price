package com.ferelin.local.database

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface CompaniesManagerHelper {

    fun insertCompanyInfo(company: Company)

    fun insertAllCompanies(list: List<Company>)

    fun updateCompanyInfo(company: Company)

    fun getAllCompanies(): Flow<List<Company>>

    fun getCompany(symbol: String): Flow<Company>

    fun deleteCompany(symbol: String)

    fun deleteCompany(company: Company)
}