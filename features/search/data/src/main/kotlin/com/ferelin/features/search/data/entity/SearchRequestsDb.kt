package com.ferelin.features.search.data.entity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SearchRequestsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(searchRequestsDBO: SearchRequestsDBO)

  @Query("SELECT * FROM `search_requests`")
  fun getAll(): Flow<List<SearchRequestsDBO>>

  @Query("DELETE FROM `search_requests`")
  suspend fun eraseAll()
}

@Entity(tableName = SEARCH_REQUESTS_DB_TABLE)
internal data class SearchRequestsDBO(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val request: String
)

internal const val SEARCH_REQUESTS_DB_TABLE = "search_requests"