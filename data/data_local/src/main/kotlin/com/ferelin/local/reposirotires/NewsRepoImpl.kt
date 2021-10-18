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

import com.ferelin.domain.entities.News
import com.ferelin.domain.repositories.NewsRepo
import com.ferelin.local.database.NewsDao
import com.ferelin.local.mappers.NewsMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NewsRepoImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsMapper: NewsMapper,
    private val dispatchersProvider: DispatchersProvider
) : NewsRepo {

    override suspend fun getAllBy(relationCompanyId: Int): List<News> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all by (companyId = $relationCompanyId)")

            return@withContext newsDao
                .getAll(relationCompanyId)
                .map(newsMapper::map)
        }

    override suspend fun insertAll(news: List<News>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (news size = ${news.size})")

            newsDao.insertAll(
                newsDBO = news.map(newsMapper::map)
            )
        }

    override suspend fun eraseBy(relationCompanyId: Int) =
        withContext(dispatchersProvider.IO) {
            Timber.d("erase by (company id = $relationCompanyId")

            newsDao.eraseBy(relationCompanyId)
        }
}