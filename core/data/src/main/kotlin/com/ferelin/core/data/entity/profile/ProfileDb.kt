package com.ferelin.core.data.entity.profile

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface ProfileDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(profilesDBO: List<ProfileDBO>)

  @Query("SELECT * FROM `profiles` WHERE id = :id")
  fun getBy(id: Int): Observable<ProfileDBO>
}

@Entity(tableName = PROFILE_DB_TABLE)
internal data class ProfileDBO(
  @PrimaryKey
  val id: Int,
  val country: String,
  val phone: String,
  val webUrl: String,
  val industry: String,
  val capitalization: String
)

internal const val PROFILE_DB_TABLE = "profiles"