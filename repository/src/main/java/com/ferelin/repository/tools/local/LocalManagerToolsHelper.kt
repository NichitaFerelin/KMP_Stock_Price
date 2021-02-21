package com.ferelin.repository.tools.local

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface LocalManagerToolsHelper {

    fun getAllCompanies(): Flow<List<Company>>

    fun insertCompanyInfo(company: Company)

    fun getFavouriteList(): Flow<Set<String>>

    suspend fun setFavouriteList(data: Set<String>)
}