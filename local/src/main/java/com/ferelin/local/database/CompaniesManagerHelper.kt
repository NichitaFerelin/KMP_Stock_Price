package com.ferelin.local.database

import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

interface CompaniesManagerHelper {

    fun insert(company: Company)

    fun insertAll(list: List<Company>)

    fun update(company: Company)

    fun getAll(): Flow<List<Company>>

    fun get(symbol: String): Flow<Company>

    fun delete(symbol: String)

    fun delete(company: Company)
}