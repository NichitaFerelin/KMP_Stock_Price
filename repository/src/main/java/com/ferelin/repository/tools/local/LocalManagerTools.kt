package com.ferelin.repository.tools.local

import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

class LocalManagerTools(
    private val mLocalManager: LocalManagerHelper
) : LocalManagerToolsHelper {

    override fun getAllCompanies(): Flow<List<Company>> {
        return mLocalManager.getAllCompanies()
    }

    override fun insertCompanyInfo(company: Company) {
        mLocalManager.insertCompanyInfo(company)
    }

    override fun getFavouriteList(): Flow<Set<String>> {
        return mLocalManager.getFavouriteList()
    }

    override suspend fun setFavouriteList(data: Set<String>) {
        mLocalManager.setFavouriteList(data)
    }
}