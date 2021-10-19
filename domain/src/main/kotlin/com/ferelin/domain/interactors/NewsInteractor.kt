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
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * [NewsInteractor] allows to interact with companies news
 * */
class NewsInteractor @Inject constructor(
    private val newsRepo: NewsRepo,
    private val newsSource: NewsSource,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) {
    /**
     * Allows to get all cached news
     * @param relationCompanyId is an company id for which need to get cached company news
     * @return list of cached news
     * */
    suspend fun getAllBy(relationCompanyId: Int): List<News> {
        return newsRepo.getAllBy(relationCompanyId)
    }

    /**
     * Allows to load actual news
     * @param companyId is a company id for which need to load company news
     * @param companyTicker is a company ticker by which need to load company news
     * @return [LoadState] with list of company news if [LoadState] is successful
     * */
    suspend fun loadBy(companyId: Int, companyTicker: String): LoadState<List<News>> {
        return newsSource.loadBy(companyId, companyTicker)
            .also { cacheIfPrepared(it, companyId) }
    }

    private fun cacheIfPrepared(loadState: LoadState<List<News>>, relationCompanyId: Int) {
        loadState.ifPrepared { preparedState ->
            externalScope.launch(dispatchersProvider.IO) {

                // Erase previous news for free up space
                newsRepo.eraseBy(relationCompanyId)

                newsRepo.insertAll(preparedState.data)
            }
        }
    }
}