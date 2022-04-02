package com.ferelin.core.data.entity.profile

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import stockprice.ProfileDBO
import stockprice.ProfileQueries

internal interface ProfileDao {
  fun getBy(id: Int): Flow<ProfileDBO>
  suspend fun insertAll(profilesDBO: List<ProfileDBO>)
}

internal class ProfileDaoImpl(
  private val queries: ProfileQueries
) : ProfileDao {
  override fun getBy(id: Int): Flow<ProfileDBO> {
    return queries.getBy(id)
      .asFlow()
      .map { it.executeAsOne() }
  }

  override suspend fun insertAll(profilesDBO: List<ProfileDBO>) {
    queries.transaction {
      profilesDBO.forEach {
        queries.insert(it)
      }
    }
  }
}