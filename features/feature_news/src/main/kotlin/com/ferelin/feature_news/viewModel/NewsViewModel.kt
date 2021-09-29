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

package com.ferelin.feature_news.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.utils.ifNotEmpty
import com.ferelin.domain.interactors.NewsInteractor
import com.ferelin.domain.interactors.NewsState
import com.ferelin.feature_news.mapper.NewsMapper
import com.ferelin.feature_news.viewData.NewsViewData
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NewsLoadState {
    class Loaded(val news: List<NewsViewData>) : NewsLoadState()
    object Loading : NewsLoadState()
    object Error : NewsLoadState()
    object None : NewsLoadState()
}

class NewsViewModel @Inject constructor(
    private val mNewsInteractor: NewsInteractor,
    private val mNewsMapper: NewsMapper,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mNewLoadState = MutableStateFlow<NewsLoadState>(NewsLoadState.None)
    val newsLoadState: StateFlow<NewsLoadState>
        get() = mNewLoadState.asStateFlow()

    var companyId = 0
    var companyTicker = ""

    fun loadData() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mNewLoadState.value = NewsLoadState.Loading

            mNewsInteractor
                .getNews(companyId)
                .ifNotEmpty { dbNews ->
                    mNewLoadState.value = NewsLoadState.Loaded(
                        dbNews.map(mNewsMapper::map)
                    )
                }

            mNewsInteractor
                .loadCompanyNews(companyTicker)
                .let { remoteNewsState ->
                    if (remoteNewsState is NewsState.Loaded) {
                        mNewLoadState.value = NewsLoadState.Loaded(
                            remoteNewsState.news.map(mNewsMapper::map)
                        )
                    } else if (mNewLoadState.value !is NewsLoadState.Loaded) {
                        mNewLoadState.value = NewsLoadState.Error
                    }
                }
        }
    }

    fun onNewsClicked(newsViewData: NewsViewData) {
        // open url
    }
}