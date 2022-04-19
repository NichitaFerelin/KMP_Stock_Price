package com.ferelin.core.data.entity.company

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import stockprice.CompanyDBO
import stockprice.CompanyQueries
import stockprice.FavoriteCompanyQueries
import stockprice.GetAll

internal typealias CompanyWithFavoriteState = GetAll

internal interface CompanyDao {
    fun getBy(id: Int): Flow<CompanyDBO>
    fun getAll(): Flow<List<CompanyDBO>>
    fun getAllFavorites(): Flow<List<GetAll>>
    suspend fun insertAll(companies: List<CompanyDBO>)
    suspend fun addToFavorites(companyId: Int)
    suspend fun eraseFromFavorites(companyId: Int)
}

internal class CompanyDaoImpl(
    private val companyQueries: CompanyQueries,
    private val favoriteCompanyQueries: FavoriteCompanyQueries
) : CompanyDao {
    override fun getBy(id: Int): Flow<CompanyDBO> {
        return companyQueries.getBy(id)
            .asFlow()
            .map { it.executeAsOne() }
    }

    override fun getAll(): Flow<List<CompanyDBO>> {
        return companyQueries.getAll()
            .asFlow()
            .mapToList()
    }

    override fun getAllFavorites(): Flow<List<CompanyWithFavoriteState>> {
        return favoriteCompanyQueries.getAll()
            .asFlow()
            .mapToList()
    }

    override suspend fun insertAll(companies: List<CompanyDBO>) {
        companyQueries.transaction {
            companies.forEach {
                companyQueries.insert(it)
            }
        }
    }

    override suspend fun addToFavorites(companyId: Int) {
        favoriteCompanyQueries.insert(null, companyId)
    }

    override suspend fun eraseFromFavorites(companyId: Int) {
        favoriteCompanyQueries.eraseBy(companyId)
    }
}