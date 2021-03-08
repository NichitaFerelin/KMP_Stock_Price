package com.ferelin.local.databases.searchesHistory

import androidx.room.*
import com.ferelin.local.model.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchesHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(search: Search)

    @Insert
    fun insertAll(list: List<Search>)

    @Update
    fun update(search: Search)

    @Query("SELECT * FROM `stockprice.searches.db`")
    fun getAll(): Flow<List<Search>>

    @Query("DELETE FROM `stockprice.searches.db` WHERE tickerName = :name")
    fun delete(name: String)

    @Delete
    fun delete(search: Search)
}