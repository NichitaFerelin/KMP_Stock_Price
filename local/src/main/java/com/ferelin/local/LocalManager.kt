package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.model.CompaniesResponse
import com.ferelin.local.model.Company
import com.ferelin.local.model.Responses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalManager(
    private val mJsonManagerHelper: JsonManagerHelper,
    private val mCompaniesManagerHelper: CompaniesManagerHelper
) : LocalManagerHelper {

    override fun insertCompany(company: Company) {
        mCompaniesManagerHelper.insertCompany(company)
    }

    override fun insertAllCompanies(list: List<Company>) {
        mCompaniesManagerHelper.insertAllCompanies(list)
    }

    override fun updateCompany(company: Company) {
        mCompaniesManagerHelper.updateCompany(company)
    }

    override fun getAllCompanies(): Flow<List<Company>> {
        return getAllCompaniesAsResponse().map { it.data }
    }

    override fun getAllCompaniesAsResponse(): Flow<CompaniesResponse> {
        return mCompaniesManagerHelper.getAllCompanies().map { databaseCompanies ->
            if (databaseCompanies.isEmpty()) {
                var localJsonCompanies = emptyList<Company>()
                getCompaniesFromJson().first { parsedList ->
                    insertAllCompanies(parsedList)
                    localJsonCompanies = parsedList
                    true
                }
                CompaniesResponse(code = Responses.LOADED_FROM_JSON, data = localJsonCompanies)
            } else CompaniesResponse(data = databaseCompanies)
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

    override fun getCompaniesFromJson(): Flow<List<Company>> {
        return mJsonManagerHelper.getCompaniesFromJson()
    }
}