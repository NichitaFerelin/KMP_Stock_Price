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

package com.ferelin.local.reposirotires

import com.ferelin.domain.entities.Profile
import com.ferelin.domain.repositories.ProfileRepo
import com.ferelin.local.database.ProfileDao
import com.ferelin.local.mappers.ProfileMapper
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(
    private val mProfileDao: ProfileDao,
    private val mProfileMapper: ProfileMapper,
    private val mCoroutineContextProvider: CoroutineContextProvider
) : ProfileRepo {

    override suspend fun getProfile(companyId: Int): Profile =
        withContext(mCoroutineContextProvider.IO) {
            return@withContext mProfileMapper.map(
                profileDBO = mProfileDao.getProfile(companyId)
            )
        }

    override suspend fun cacheProfiles(profiles: List<Profile>) =
        withContext(mCoroutineContextProvider.IO) {
            mProfileDao.insertProfiles(profiles.map(mProfileMapper::map))
        }
}