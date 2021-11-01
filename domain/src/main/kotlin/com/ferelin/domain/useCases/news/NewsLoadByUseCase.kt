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

package com.ferelin.domain.useCases.news

import com.ferelin.domain.entities.News
import com.ferelin.domain.repositories.NewsRepo
import com.ferelin.domain.sources.NewsSource
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * [NewsLoadByUseCase] allows to interact with companies news
 * */
class NewsLoadByUseCase @Inject constructor(
    private val newsRepo: NewsRepo,
    private val newsSource: NewsSource,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    /**
     * Allows to load actual news
     * @param relationCompanyId is a company id for which need to load company news
     * @param companyTicker is a company ticker by which need to load company news
     * @return [LoadState] with list of company news if [LoadState] is successful
     * */
    suspend fun loadBy(relationCompanyId: Int, companyTicker: String): LoadState<List<News>> {
        return newsSource
            .loadBy(relationCompanyId, companyTicker)
            .also { cacheIfPrepared(relationCompanyId, it) }
    }

    private fun cacheIfPrepared(relationCompanyId: Int, loadState: LoadState<List<News>>) {
        loadState.ifPrepared { preparedState ->
            externalScope.launch {
                // Erase previous
                newsRepo.eraseBy(relationCompanyId)

                newsRepo.insertAll(preparedState.data)
            }
        }
    }
}