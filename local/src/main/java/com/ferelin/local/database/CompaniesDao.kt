package com.ferelin.local.database

import androidx.room.*
import com.ferelin.local.model.Company
import kotlinx.coroutines.flow.Flow

@Dao
interface CompaniesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(company: Company)

    @Insert
    fun insertAll(list: List<Company>)

    @Update
    fun update(company: Company)

    @Query("SELECT * FROM `stockprice.companies.db`")
    fun getAll(): Flow<List<Company>>

    @Query("SELECT * FROM `stockprice.companies.db` WHERE symbol IN (:symbol)")
    fun get(symbol: String): Flow<Company>

    @Query("DELETE FROM `stockprice.companies.db` WHERE symbol = :symbol")
    fun delete(symbol: String)

    @Delete
    fun delete(company: Company)
}