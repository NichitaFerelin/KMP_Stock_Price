package com.ferelin.core.data.entity.companyNews

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.CompanyNewsDBO
import stockprice.CompanyNewsQueries

internal interface CompanyNewsDao {
    fun getAllBy(companyId: Int): Flow<List<CompanyNewsDBO>>
    suspend fun insertAll(companyNewsDbo: List<CompanyNewsDBO>)
    suspend fun eraseAllBy(companyId: Int)
}

internal class CompanyNewsDaoImpl(
    private val queries: CompanyNewsQueries
) : CompanyNewsDao {
    override fun getAllBy(companyId: Int): Flow<List<CompanyNewsDBO>> {
        return queries.getAllBy(companyId)
            .asFlow()
            .mapToList()
    }

    override suspend fun insertAll(companyNewsDbo: List<CompanyNewsDBO>) {
        queries.transaction {
            companyNewsDbo.forEach {
                queries.insert(it)
            }
        }
    }

    override suspend fun eraseAllBy(companyId: Int) {
        queries.eraseAllBy(companyId)
    }
}