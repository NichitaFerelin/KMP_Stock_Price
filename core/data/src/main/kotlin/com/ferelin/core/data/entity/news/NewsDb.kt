package com.ferelin.core.data.entity.news

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface NewsDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(newsDBO: List<NewsDBO>)

  @Query("SELECT * FROM `news` WHERE companyId = :companyId")
  fun getAllBy(companyId: Int): Observable<List<NewsDBO>>

  @Query("DELETE FROM `news` WHERE companyId = :companyId")
  fun eraseAllBy(companyId: Int)
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
  val date: Long
)

internal const val NEWS_DB_TABLE = "news"