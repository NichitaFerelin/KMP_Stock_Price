package com.ferelin.stockprice.shared.commonMain.data.entity.profile

import com.ferelin.stockprice.db.ProfileDBO
import com.ferelin.stockprice.db.ProfileQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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