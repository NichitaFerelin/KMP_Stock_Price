package com.ferelin.local.database

import androidx.room.*
import com.ferelin.local.models.Company
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

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

    fun getAllCompanies() = getAll().distinctUntilChanged()

    @Delete
    fun delete(company: Company)
}