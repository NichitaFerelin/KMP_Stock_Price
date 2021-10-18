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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.R
import com.ferelin.core.adapter.base.ifLinear
import com.ferelin.core.adapter.base.scrollToTopWithCustomAnim
import com.ferelin.core.params.NewsParams
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_news.adapter.NewsItemDecoration
import com.ferelin.feature_news.databinding.FragmentNewsBinding
import com.ferelin.feature_news.viewModel.NewsViewModel
import com.ferelin.shared.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsFragment : BaseFragment<FragmentNewsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
        get() = FragmentNewsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<NewsViewModel>

    private val viewModel: NewsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private var fadeOut: Animation? = null
    private var fadeIn: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        with(viewBinding) {
            recyclerViewNews.apply {
                adapter = viewModel.newsAdapter
                addItemDecoration(NewsItemDecoration(requireContext()))
            }
            fab.setOnClick(this@NewsFragment::scrollToTop)
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            observeNewsState()
        }
    }

    override fun onDestroyView() {
        fadeIn?.invalidate()
        fadeOut?.invalidate()
        super.onDestroyView()
    }

    private suspend fun observeNewsState() {
        viewModel.newsLoadState.collect { loadState ->
            when (loadState) {
                is LoadState.None -> viewModel.loadData()
                is LoadState.Loading -> showProgressBar()
                is LoadState.Prepared -> hideProgressBar()
                is LoadState.Error -> onError()
            }
        }
    }

    private fun onError() {
        if(!viewModel.isNetworkAvailable) {
            showSnackbar(getString(R.string.messageNetworkNotAvailable))
        } else {
            showTempSnackbar(getString(R.string.errorUndefined))
        }
    }

    private suspend fun showProgressBar() {
        withContext(Dispatchers.Main) {
            viewBinding.progressBar.isVisible = true
        }
    }

    private suspend fun hideProgressBar() {
        withContext(Dispatchers.Main) {
            viewBinding.progressBar.isVisible = false
        }
    }

    private fun scrollToTop() {
        viewBinding.recyclerViewNews.layoutManager.ifLinear { layoutManager ->
            if (layoutManager.findFirstVisibleItemPosition() < SCROLL_WITH_ANIM_AFTER) {
                viewBinding.recyclerViewNews.smoothScrollToPosition(0)
            } else {
                initFadeAnims()
                viewBinding.recyclerViewNews.scrollToTopWithCustomAnim(
                    fadeIn!!,
                    fadeOut!!,
                    null
                )
            }
        }
    }

    private fun initFadeAnims() {
        if (fadeIn == null) {
            fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[NEWS_PARAMS_KEY]?.let { params ->
            if (params is NewsParams) {
                viewModel.newsParams = params
            }
        }
    }

    companion object {

        private const val NEWS_PARAMS_KEY = "n"
        private const val SCROLL_WITH_ANIM_AFTER = 7

        fun newInstance(data: Any?): NewsFragment {
            return NewsFragment().also {
                if (data is NewsParams) {
                    it.arguments = bundleOf(NEWS_PARAMS_KEY to data)
                }
            }
        }
    }
}