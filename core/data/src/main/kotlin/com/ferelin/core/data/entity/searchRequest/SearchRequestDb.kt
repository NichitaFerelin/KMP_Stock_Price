package com.ferelin.core.data.entity.searchRequest

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface SearchRequestDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(searchRequestsDBO: SearchRequestDBO)

  @Query("SELECT * FROM `search_requests`")
  fun getAll(): Observable<List<SearchRequestDBO>>

  @Delete
  fun erase(searchRequestDBO: SearchRequestDBO)

  @Query("DELETE FROM `search_requests`")
  fun eraseAll()
}

@Entity(tableName = SEARCH_REQUESTS_DB_TABLE)
internal data class SearchRequestDBO(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val request: String
)

internal const val SEARCH_REQUESTS_DB_TABLE = "search_requests"