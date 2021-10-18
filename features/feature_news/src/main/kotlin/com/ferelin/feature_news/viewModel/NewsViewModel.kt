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
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.params.NewsParams
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.utils.ifNotEmpty
import com.ferelin.domain.entities.News
import com.ferelin.domain.interactors.NewsInteractor
import com.ferelin.feature_news.adapter.createNewsAdapter
import com.ferelin.feature_news.mapper.NewsMapper
import com.ferelin.feature_news.viewData.NewsViewData
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.ferelin.shared.NetworkListener
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsViewModel @Inject constructor(
    private val newsInteractor: NewsInteractor,
    private val newsMapper: NewsMapper,
    private val networkResolver: NetworkResolver
) : ViewModel(), NetworkListener {

    private val _newsLoadState = MutableStateFlow<LoadState<List<NewsViewData>>>(LoadState.None())
    val newsLoadState: StateFlow<LoadState<List<NewsViewData>>> = _newsLoadState.asStateFlow()

    val isNetworkAvailable: Boolean
        get() = networkResolver.isNetworkAvailable

    var newsParams = NewsParams()

    val newsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createNewsAdapter(this::onNewsItemClick)
        ).apply { setHasStableIds(true) }
    }

    init {
        networkResolver.registerNetworkListener(this)
    }

    override suspend fun onNetworkAvailable() {
        viewModelScope.launch {
            _newsLoadState.value.ifPrepared {
                loadFromNetwork()
            } ?: loadData()
        }
    }

    override suspend fun onNetworkLost() {
        // do nothing
    }

    override fun onCleared() {
        networkResolver.unregisterNetworkListener(this)
        super.onCleared()
    }

    fun loadData() {
        viewModelScope.launch {
            _newsLoadState.value = LoadState.Loading()

            loadFromDb()
            loadFromNetwork()
        }
    }

    private suspend fun loadFromDb() {
        newsInteractor
            .getAllBy(newsParams.companyId)
            .ifNotEmpty { dbNews -> onNewsChanged(dbNews) }
    }

    private suspend fun loadFromNetwork() {
        newsInteractor
            .loadBy(newsParams.companyId, newsParams.companyTicker)
            .let { remoteNewsState ->
                if (remoteNewsState is LoadState.Prepared) {
                    onNewsChanged(remoteNewsState.data)
                } else if (_newsLoadState.value !is LoadState.Prepared) {
                    _newsLoadState.value = LoadState.Error()
                }
            }
    }

    private suspend fun onNewsChanged(news: List<News>) {
        val mappedNews = news.map(newsMapper::map)
        _newsLoadState.value = LoadState.Prepared(mappedNews)

        withContext(Dispatchers.Main) {
            newsAdapter.setData(mappedNews)
        }
    }

    private fun onNewsItemClick(newsViewData: NewsViewData) {

    }
}