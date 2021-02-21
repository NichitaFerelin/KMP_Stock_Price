package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.Company
import com.ferelin.local.preferences.PreferencesManagerHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalManager(
    private val mJsonManagerHelper: JsonManagerHelper,
    private val mCompaniesManagerHelper: CompaniesManagerHelper,
    private val mPreferencesManagerHelper: PreferencesManagerHelper
) : LocalManagerHelper {

    override fun insertCompanyInfo(company: Company) {
        mCompaniesManagerHelper.insertCompanyInfo(company)
    }

    override fun insertAllCompanies(list: List<Company>) {
        mCompaniesManagerHelper.insertAllCompanies(list)
    }

    override fun updateCompanyInfo(company: Company) {
        mCompaniesManagerHelper.updateCompanyInfo(company)
    }

    override fun getAllCompanies(): Flow<List<Company>> {
        return mCompaniesManagerHelper.getAllCompanies().map { databaseCompanies ->
            if (databaseCompanies.isEmpty()) {
                var localJsonCompanies = emptyList<Company>()
                getCompaniesFromJson().first { parsedList ->
                    insertAllCompanies(parsedList)
                    localJsonCompanies = parsedList
                    true
                }
                localJsonCompanies
            } else databaseCompanies
        }
    }

    override fun getCompany(symbol: String): Flow<Company> {
        return mCompaniesManagerHelper.getCompany(symbol)
    }

    override fun deleteCompany(symbol: String) {
        mCompaniesManagerHelper.deleteCompany(symbol)
    }

    override fun deleteCompany(company: Company) {
        mCompaniesManagerHelper.deleteCompany(company)
    }

    override fun getFavouriteList(): Flow<Set<String>> {
        return mPreferencesManagerHelper.getFavouriteList()
    }

    override suspend fun setFavouriteList(data: Set<String>) {
        mPreferencesManagerHelper.setFavouriteList(data)
    }

    override fun getCompaniesFromJson(): Flow<List<Company>> {
        return mJsonManagerHelper.getCompaniesFromJson()
    }
}