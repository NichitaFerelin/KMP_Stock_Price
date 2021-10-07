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

package com.ferelin.domain.interactors

import com.ferelin.domain.entities.News
import com.ferelin.domain.repositories.NewsRepo
import com.ferelin.domain.sources.NewsSource
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

sealed class NewsState {
    class Loaded(val news: List<News>) : NewsState()
    object Error : NewsState()
}

class NewsInteractor @Inject constructor(
    private val mNewsRepo: NewsRepo,
    private val mNewsSource: NewsSource,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    suspend fun getNews(companyId: Int): List<News> {
        return mNewsRepo.getNews(companyId)
    }

    suspend fun loadCompanyNews(companyId: Int, companyTicker: String): NewsState {
        return mNewsSource.loadCompanyNews(companyId, companyTicker)
            .also { cacheIfLoaded(it) }
    }

    private fun cacheIfLoaded(responseState: NewsState) {
        mExternalScope.launch(mDispatchersProvider.IO) {
            if (responseState is NewsState.Loaded) {
                for (news in responseState.news) {
                    mNewsRepo.cacheNews(news)
                }
            }
        }
    }
}