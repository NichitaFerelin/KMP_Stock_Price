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

package com.ferelin.feature_news.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.core.base.recyclerAdapter.BaseRecyclerAdapter
import com.ferelin.feature_news.adapter.NewsItemDecoration
import com.ferelin.feature_news.adapter.createNewsAdapter
import com.ferelin.feature_news.databinding.FragmentNewsBinding
import com.ferelin.feature_news.viewData.NewsViewData
import com.ferelin.feature_news.viewModel.NewsLoadState
import com.ferelin.feature_news.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

// TODO temp values
val companyId = 1
val companyTicker = "MSFT"

class NewsFragment : BaseFragment<FragmentNewsBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
        get() = FragmentNewsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<NewsViewModel>

    private val mViewModel: NewsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )
    private val mNewsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createNewsAdapter(this::onNewsClicked)
        )
    }

    override fun initUi() {
        mViewBinding.recyclerViewNews.adapter = mNewsAdapter
        mViewBinding.recyclerViewNews.addItemDecoration(
            NewsItemDecoration(requireContext())
        )
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mViewModel.newsLoadState.collect { loadState ->
                when (loadState) {
                    is NewsLoadState.None -> {
                        mViewModel.loadData(companyId, companyTicker)
                    }
                    is NewsLoadState.Loaded -> {
                        mNewsAdapter.replaceAsNew(loadState.news)
                    }
                    is NewsLoadState.Loading -> {
                        // update ui
                    }
                    is NewsLoadState.Error -> {
                        // update ui
                    }
                }
            }
        }
    }

    private fun onNewsClicked(newsViewData: NewsViewData) {

    }
}