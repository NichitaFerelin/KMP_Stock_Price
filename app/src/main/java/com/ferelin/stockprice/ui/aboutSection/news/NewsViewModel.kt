package com.ferelin.stockprice.ui.aboutSection.news

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

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsViewModel(val selectedCompany: AdaptiveCompany) : BaseViewModel() {

    private val mNewsRecyclerAdapter = NewsRecyclerAdapter()
    val newsRecyclerAdapter: NewsRecyclerAdapter
        get() = mNewsRecyclerAdapter

    private val mStateNews = MutableStateFlow<AdaptiveCompanyNews?>(null)
    val stateNews: StateFlow<AdaptiveCompanyNews?>
        get() = mStateNews

    private val mStateIsDataLoading = MutableStateFlow(false)
    val stateIsDataLoading: StateFlow<Boolean>
        get() = mStateIsDataLoading

    private var mIsNetworkResponded = false

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadCompanyNewsError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            collectStateIsNetworkAvailable()
        }
    }

    private suspend fun collectStateIsNetworkAvailable() {
        mDataInteractor.stateIsNetworkAvailable.collect { isAvailable ->
            if (isAvailable && !mIsNetworkResponded) {
                mStateIsDataLoading.value = true
                collectCompanyNews()
            } else mStateIsDataLoading.value = false
        }
    }

    private suspend fun collectCompanyNews() {
        val selectedCompanySymbol = selectedCompany.companyProfile.symbol
        mDataInteractor.loadCompanyNewsFromNetwork(selectedCompanySymbol).collect { company ->
            onNewsLoaded(company)
        }
    }

    private fun onNewsLoaded(company: AdaptiveCompany) {
        mStateIsDataLoading.value = false
        mIsNetworkResponded = true
        mStateNews.value = company.companyNews
    }
}