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
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_news.adapter.NewsItemDecoration
import com.ferelin.feature_news.databinding.FragmentNewsBinding
import com.ferelin.feature_news.viewModel.NewsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsFragment : BaseFragment<FragmentNewsBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNewsBinding
        get() = FragmentNewsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<NewsViewModel>

    private val mViewModel: NewsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private var mFadeOut: Animation? = null
    private var mFadeIn: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { unpackArgs(it) }
    }

    override fun initUi() {
        with(mViewBinding) {
            recyclerViewNews.apply {
                adapter = mViewModel.newsAdapter
                addItemDecoration(NewsItemDecoration(requireContext()))
            }
            fab.setOnClick(this@NewsFragment::scrollToTop)
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mDispatchersProvider.IO) {
            observeNewsState()
        }
    }

    override fun onDestroyView() {
        mFadeIn?.invalidate()
        mFadeOut?.invalidate()
        super.onDestroyView()
    }

    private suspend fun observeNewsState() {
        mViewModel.newsLoadState.collect { loadState ->
            when (loadState) {
                is LoadState.None -> mViewModel.loadData()
                is LoadState.Loading -> showProgressBar()
                is LoadState.Prepared -> hideProgressBar()
            }
        }
    }

    private suspend fun showProgressBar() {
        withContext(mDispatchersProvider.Main) {
            mViewBinding.progressBar.isVisible = true
        }
    }

    private suspend fun hideProgressBar() {
        withContext(mDispatchersProvider.Main) {
            mViewBinding.progressBar.isVisible = false
        }
    }

    private fun scrollToTop() {
        mViewBinding.recyclerViewNews.layoutManager.ifLinear { layoutManager ->
            if (layoutManager.findFirstVisibleItemPosition() < sScrollWithAnimAfter) {
                mViewBinding.recyclerViewNews.smoothScrollToPosition(0)
            } else {
                initFadeAnims()
                mViewBinding.recyclerViewNews.scrollToTopWithCustomAnim(
                    mFadeIn!!,
                    mFadeOut!!,
                    null
                )
            }
        }
    }

    private fun initFadeAnims() {
        if (mFadeIn == null) {
            mFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            mFadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }

    private fun unpackArgs(args: Bundle) {
        args[sNewsParamsKey]?.let { params ->
            if (params is NewsParams) {
                mViewModel.newsParams = params
            }
        }
    }

    companion object {

        private const val sNewsParamsKey = "n"
        private const val sScrollWithAnimAfter = 7

        fun newInstance(data: Any?): NewsFragment {
            return NewsFragment().also {
                if (data is NewsParams) {
                    it.arguments = bundleOf(sNewsParamsKey to data)
                }
            }
        }
    }
}