package com.ferelin.local.databases.companies

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

class CompaniesManager(companiesDatabase: CompaniesDatabase) : CompaniesManagerHelper {

    private val mCompaniesDao = companiesDatabase.companiesDao()

    override fun insertCompany(company: Company) {
        mCompaniesDao.insert(company)
    }

    override fun insertAllCompanies(list: List<Company>) {
        mCompaniesDao.insertAll(list)
    }

    override fun updateCompany(company: Company) {
        mCompaniesDao.update(company)
    }

    override fun getAllCompanies(): Flow<List<Company>> {
        return mCompaniesDao.getAll()
    }

    override fun getCompany(symbol: String): Flow<Company> {
        return mCompaniesDao.get(symbol)
    }

    override fun deleteCompany(symbol: String) {
        mCompaniesDao.delete(symbol)
    }

    override fun deleteCompany(company: Company) {
        mCompaniesDao.delete(company)
    }
}


