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
    private val mNewsDao: NewsDao,
    private val mNewsMapper: NewsMapper,
    private val mDispatchersProvider: DispatchersProvider
) : NewsRepo {

    override suspend fun getNews(companyId: Int): List<News> =
        withContext(mDispatchersProvider.IO) {
            Timber.d("get news by companyId (companyId = $companyId)")
            return@withContext mNewsDao
                .getAllNews(companyId)
                .map(mNewsMapper::map)
        }

    override suspend fun cacheNews(news: News) =
        withContext(mDispatchersProvider.IO) {
            Timber.d("cache news (news = $news)")
            mNewsDao.insertNews(
                newsDBO = mNewsMapper.map(news)
            )
        }
}