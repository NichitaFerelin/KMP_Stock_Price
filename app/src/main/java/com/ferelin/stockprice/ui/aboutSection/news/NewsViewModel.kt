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

package com.ferelin.stockprice.ui.aboutSection.news

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.CompanyNews
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.aboutSection.news.adapter.NewsRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(val selectedCompany: AdaptiveCompany) : BaseViewModel() {

    val newsRecyclerAdapter = NewsRecyclerAdapter()

    private val mStateCompanyNews =
        MutableStateFlow<DataNotificator<CompanyNews>>(DataNotificator.None())
    val stateCompanyNews: StateFlow<DataNotificator<CompanyNews>>
        get() = mStateCompanyNews.asStateFlow()

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadCompanyNewsError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareInitialNewsState()
            loadCompanyNews()
        }
    }

    private suspend fun prepareInitialNewsState() {
        if (selectedCompany.companyNews.ids.isNotEmpty()) {
            mStateCompanyNews.value = DataNotificator.DataPrepared(selectedCompany.companyNews)
            withContext(mCoroutineContext.Main) {
                newsRecyclerAdapter.setData(selectedCompany.companyNews)
            }
        }
    }

    private suspend fun loadCompanyNews() {
        mDataInteractor.loadCompanyNews(selectedCompany.companyProfile.symbol)
            .collect { notificator ->
                when (notificator) {
                    is DataNotificator.DataPrepared -> {
                        mStateCompanyNews.value = DataNotificator.DataPrepared(notificator.data!!)
                        withContext(mCoroutineContext.Main) {
                            newsRecyclerAdapter.setData(selectedCompany.companyNews)
                        }
                    }
                    is DataNotificator.Loading -> {
                        if (mStateCompanyNews.value !is DataNotificator.DataPrepared) {
                            mStateCompanyNews.value = DataNotificator.Loading()
                        }
                    }
                    else -> {
                        if (mStateCompanyNews.value !is DataNotificator.DataPrepared) {
                            mStateCompanyNews.value = DataNotificator.None()
                        }
                    }
                }
            }
    }
}