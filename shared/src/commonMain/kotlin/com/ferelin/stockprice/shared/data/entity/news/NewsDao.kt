package com.ferelin.stockprice.shared.data.entity.news

import com.ferelin.stockprice.db.NewsDBO
import com.ferelin.stockprice.db.NewsQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

internal interface NewsDao {
    fun getAllBy(companyId: Int): Flow<List<NewsDBO>>
    suspend fun insertAll(newsDBO: List<NewsDBO>)
    suspend fun eraseAllBy(companyId: Int)
}

internal class NewsDaoImpl(
    private val queries: NewsQueries
) : NewsDao {
    override fun getAllBy(companyId: Int): Flow<List<NewsDBO>> {
        return queries.getAllBy(companyId)
            .asFlow()
            .mapToList()
    }

    override suspend fun insertAll(newsDBO: List<NewsDBO>) {
        queries.transaction {
            newsDBO.forEach {
                queries.insert(it)
            }
        }
    }

    override suspend fun eraseAllBy(companyId: Int) {
        queries.eraseAllBy(companyId)
    }
}