package com.ferelin.core.data.entity.news

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface NewsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(newsDBO: List<NewsDBO>)

  @Query("SELECT * FROM `news` WHERE companyId = :companyId")
  fun getAllBy(companyId: Int): Flow<List<NewsDBO>>

  @Query("DELETE FROM `news` WHERE companyId = :companyId")
  suspend fun eraseAllBy(companyId: Int)
}

@Entity(tableName = NEWS_DB_TABLE)
internal data class NewsDBO(
  @PrimaryKey
  val id: String,
  val companyId: Int,
  val headline: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val date: Double
)

internal const val NEWS_DB_TABLE = "news"