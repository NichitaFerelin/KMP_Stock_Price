package com.ferelin.local.database

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

class CompaniesManager(companiesDatabase: CompaniesDatabase) : CompaniesManagerHelper {

    private val mCompaniesDao = companiesDatabase.companiesDao()

    override fun insert(company: Company) {
        mCompaniesDao.insert(company)
    }

    override fun insertAll(list: List<Company>) {
        mCompaniesDao.insertAll(list)
    }

    override fun update(company: Company) {
        mCompaniesDao.update(company)
    }

    override fun getAll(): Flow<List<Company>> {
        return mCompaniesDao.getAll()
    }

    override fun get(symbol: String): Flow<Company> {
        return mCompaniesDao.get(symbol)
    }

    override fun delete(symbol: String) {
        mCompaniesDao.delete(symbol)
    }

    override fun delete(company: Company) {
        mCompaniesDao.delete(company)
    }
}


