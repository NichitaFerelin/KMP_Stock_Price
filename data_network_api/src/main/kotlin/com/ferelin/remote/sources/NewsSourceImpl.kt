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

package com.ferelin.remote.sources

import com.ferelin.domain.interactors.NewsState
import com.ferelin.domain.sources.NewsSource
import com.ferelin.remote.entities.NewsApi
import com.ferelin.remote.mappers.NewsMapper
import com.ferelin.remote.utils.withExceptionHandle
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext")
class NewsSourceImpl @Inject constructor(
    private val mCompanyNewsApi: NewsApi,
    private val mNewsMapper: NewsMapper,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("FinnhubToken") private val mApiToken: String
) : NewsSource {

    override suspend fun loadCompanyNews(
        companyTicker: String,
        from: String,
        to: String
    ): NewsState = withContext(mCoroutineContextProvider.IO) {
        withExceptionHandle(
            request = {
                mCompanyNewsApi
                    .getNews(companyTicker, mApiToken, from, to)
                    .execute()
            },
            onSuccess = { responseBody ->
                NewsState.Loaded(responseBody.map(mNewsMapper::map))
            },
            onFail = { NewsState.Error }
        )
    }
}