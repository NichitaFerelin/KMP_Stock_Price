/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.data_local.reposirotires

import com.ferelin.domain.entities.Profile
import com.ferelin.domain.repositories.ProfileRepo
import com.ferelin.data_local.database.ProfileDao
import com.ferelin.data_local.mappers.ProfileMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val profileMapper: ProfileMapper,
    private val dispatchersProvider: DispatchersProvider
) : ProfileRepo {

    override suspend fun insertAll(profiles: List<Profile>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (profiles size = ${profiles.size})")

            profileDao.insertAll(
                profilesDBO = profiles.map(profileMapper::map)
            )
        }

    override suspend fun getBy(relationCompanyId: Int): Profile =
        withContext(dispatchersProvider.IO) {
            Timber.d("get by (relation company id = $relationCompanyId)")

            val profile = profileDao.get(relationCompanyId)
            return@withContext profileMapper.map(profile)
        }
}