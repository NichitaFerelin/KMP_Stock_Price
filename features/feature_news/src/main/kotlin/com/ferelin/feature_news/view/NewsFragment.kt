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

import android.os.Bundle
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
import com.ferelin.feature_news.viewModel.NewsLoadState
import com.ferelin.feature_news.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

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
            createNewsAdapter(mViewModel::onNewsClicked)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
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
                        mViewModel.loadData()
                    }
                    is NewsLoadState.Loaded -> {
                        mNewsAdapter.replaceAsNew(loadState.news)
                    }
                    is NewsLoadState.Loading -> {

                    }
                    is NewsLoadState.Error -> {

                    }
                }
            }
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[ARGS_COMPANY_ID_KEY]?.let { companyId ->
            if (companyId is Int) {
                mViewModel.companyId = companyId
            }
        }
        args[ARGS_COMPANY_TICKER_KEY]?.let { companyTicker ->
            if (companyTicker is String) {
                mViewModel.companyTicker = companyTicker
            }
        }
    }

    companion object {

        const val ARGS_COMPANY_ID_KEY = "id"
        const val ARGS_COMPANY_TICKER_KEY = "ticker"

        fun newInstance(args: Bundle): NewsFragment {
            return NewsFragment().apply {
                arguments = args
            }
        }
    }
}