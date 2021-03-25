package com.ferelin.local.database

import com.ferelin.local.models.Company
import kotlinx.coroutines.flow.Flow

open class CompaniesManager(private val mCompaniesDao: CompaniesDao) : CompaniesManagerHelper {

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

    override fun deleteCompany(company: Company) {
        mCompaniesDao.delete(company)
    }
}


