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

package com.ferelin.feature_search.view

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.MotionManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseStocksFragment
import com.ferelin.core.viewModel.StocksMode
import com.ferelin.feature_search.R
import com.ferelin.feature_search.adapter.itemDecoration.SearchItemDecoration
import com.ferelin.feature_search.adapter.itemDecoration.SearchItemDecorationLandscape
import com.ferelin.feature_search.databinding.FragmentSearchBinding
import com.ferelin.feature_search.viewModel.SearchViewModel
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class SearchFragment : BaseStocksFragment<FragmentSearchBinding, SearchViewModel>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    override val mViewModel: SearchViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override val mStocksMode = StocksMode.ALL

    private val mBackPressedCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // TODO >= 0.50F
                if (mViewBinding.root.progress == 1F) {
                    mViewBinding.editTextSearch.setText("")
                } else {
                    this.remove()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private var mScaleIn: Animation? = null
    private var mScaleOut: Animation? = null

    private var mKeyboardTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ true
        ).apply {
            duration = 200L
        }
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ false
        ).apply {
            duration = 200L
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stocksRecyclerView = mViewBinding.recyclerViewSearchResults
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            mKeyboardTimer = Timer().apply {
                schedule(timerTask {
                    viewLifecycleOwner.lifecycleScope.launch(mDispatchersProvider.Main) {
                        showKeyboard(mViewBinding.editTextSearch)
                    }
                }, 400L)
            }
        }
    }

    override fun initUi() {
        super.initUi()
        with(mViewBinding) {
            root.progress = mViewModel.transitionState

            recyclerViewSearchedHistory.apply {
                adapter = mViewModel.searchRequestsAdapter
                addItemDecoration(provideItemDecoration())
            }
            recyclerViewPopularRequests.apply {
                adapter = mViewModel.popularSearchRequestsAdapter
                addItemDecoration(provideItemDecoration())
            }
        }
    }

    override fun initUx() {
        super.initUx()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            mBackPressedCallback
        )

        with(mViewBinding) {
            imageViewIconClose.setOnClick(this@SearchFragment::onCloseIconClick)
            imageViewBack.setOnClick(this@SearchFragment::onBackClick)

            editTextSearch.addTextChangedListener {
                val searchRequest = it.toString()
                mViewModel.onSearchTextChanged(searchRequest)
                onSearchTextChanged(searchRequest)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mDispatchersProvider.IO) {
            launch { observeSearchRequestsState() }
            launch { observeSearchResults() }
            launch { observeSearchTextChanges() }
        }
    }

    override fun onDestroyView() {
        mScaleIn?.invalidate()
        mScaleOut?.invalidate()
        mKeyboardTimer?.cancel()
        hideKeyboard()
        super.onDestroyView()
    }

    private suspend fun observeSearchRequestsState() {
        mViewModel.searchRequestsState.collect { loadState ->
            if (loadState is LoadState.None) {
                mViewModel.loadSearchRequests()
            }
        }
    }

    private suspend fun observeSearchResults() {
        mViewModel.searchResultsExists.collect { resultsExists ->
            withContext(mDispatchersProvider.Main) {
                if (resultsExists) {
                    mViewModel.transitionState = TRANSITION_END
                    mViewBinding.root.transitionToEnd()
                } else {
                    mViewModel.transitionState = TRANSITION_START
                    mViewBinding.root.transitionToStart()
                }
            }
        }
    }

    private suspend fun observeSearchTextChanges() {
        mViewModel.searchTextChanged.collect { searchText ->
            withContext(mDispatchersProvider.Main) {
                mViewBinding.editTextSearch.setText(searchText)
                mViewBinding.editTextSearch.setSelection(searchText.length)
            }
        }
    }

    private fun provideItemDecoration(): SearchItemDecoration {
        return requireContext().let {
            if (it.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                SearchItemDecorationLandscape(it)
            } else {
                SearchItemDecoration(it)
            }
        }
    }

    private fun onSearchTextChanged(searchText: String) {
        if (searchText.isEmpty()) {
            hideCloseIcon()
        } else {
            showCloseIcon()
        }
    }

    private fun onBackClick() {
        if (mViewBinding.root.progress == 0F) {
            mViewModel.onBackClick()
        } else {
            mViewBinding.root.addTransitionListener(object : MotionManager() {
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    mViewModel.onBackClick()
                }
            })
            mViewBinding.root.transitionToStart()
        }
    }

    private fun hideCloseIcon() {
        // TODO const
        if (mViewBinding.imageViewIconClose.scaleX == 1F) {

            if (mScaleOut == null) {
                mScaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    mViewBinding.imageViewIconClose.scaleX = 0F
                    mViewBinding.imageViewIconClose.scaleY = 0F
                }
            }

            mScaleOut!!.setAnimationListener(callback)
            mViewBinding.imageViewIconClose.startAnimation(mScaleOut!!)
        }
    }

    private fun showCloseIcon() {
        // TODO const
        if (mViewBinding.imageViewIconClose.scaleX == 0F) {

            if (mScaleIn == null) {
                mScaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mViewBinding.imageViewIconClose.scaleX = 1F
                    mViewBinding.imageViewIconClose.scaleY = 1F
                }
            }

            mScaleIn!!.setAnimationListener(callback)
            mViewBinding.imageViewIconClose.startAnimation(mScaleIn!!)
        }
    }

    private fun onCloseIconClick() {
        mViewBinding.editTextSearch.setText("")
    }

    companion object {

        const val TRANSITION_START = 0f
        const val TRANSITION_END = 1f

        fun newInstance(data: Any?): SearchFragment {
            return SearchFragment()
        }
    }
}