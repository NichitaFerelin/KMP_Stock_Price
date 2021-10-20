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
import com.ferelin.core.utils.*
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.MotionManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.view.BaseStocksFragment
import com.ferelin.feature_search.R
import com.ferelin.feature_search.adapter.itemDecoration.SearchItemDecoration
import com.ferelin.feature_search.adapter.itemDecoration.SearchItemDecorationLandscape
import com.ferelin.feature_search.databinding.FragmentSearchBinding
import com.ferelin.feature_search.viewModel.SearchViewModel
import com.ferelin.shared.LoadState
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class SearchFragment : BaseStocksFragment<FragmentSearchBinding, SearchViewModel>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    override val viewModel: SearchViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val backPressedCallback by lazy(LazyThreadSafetyMode.NONE) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewBinding.root.isAtEnd) {
                    viewBinding.editTextSearch.setText("")
                } else {
                    this.remove()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private var scaleIn: Animation? = null
    private var scaleOut: Animation? = null

    private var keyboardTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stocksRecyclerView = viewBinding.recyclerViewSearchResults
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            keyboardTimer = Timer().apply {
                schedule(timerTask {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        showKeyboard(viewBinding.editTextSearch)
                    }
                }, 400L)
            }
        }
    }

    override fun initUi() {
        super.initUi()
        with(viewBinding) {
            root.progress = viewModel.transitionState

            recyclerViewSearchedHistory.apply {
                adapter = viewModel.searchRequestsAdapter
                addItemDecoration(provideItemDecoration())
            }
            recyclerViewPopularRequests.apply {
                adapter = viewModel.popularSearchRequestsAdapter
                addItemDecoration(provideItemDecoration())
            }
        }
    }

    override fun initUx() {
        super.initUx()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        with(viewBinding) {
            imageViewIconClose.setOnClick(this@SearchFragment::onCloseIconClick)
            imageViewBack.setOnClick(this@SearchFragment::onBackClick)

            editTextSearch.addTextChangedListener {
                val searchRequest = it.toString()
                viewModel.onSearchTextChanged(searchRequest)
                onSearchTextChanged(searchRequest)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observeSearchRequestsState() }
            launch { observeSearchResults() }
            launch { observeSearchTextChanges() }
        }
    }

    override fun onDestroyView() {
        scaleIn?.invalidate()
        scaleOut?.invalidate()

        keyboardTimer?.cancel()
        hideKeyboard()

        val recyclerViewPopulars = viewBinding.recyclerViewPopularRequests
        val recyclerViewSearch = viewBinding.recyclerViewSearchedHistory

        // Graphic bug
        withTimer {
            recyclerViewPopulars.adapter = null
            recyclerViewSearch.adapter = null
        }

        super.onDestroyView()
    }

    private suspend fun observeSearchRequestsState() {
        viewModel.searchRequestsState.collect { loadState ->
            if (loadState is LoadState.None) {
                viewModel.loadSearchRequests()
            }
        }
    }

    private suspend fun observeSearchResults() {
        viewModel.searchResultsExists.collect { resultsExists ->
            withContext(Dispatchers.Main) {
                if (resultsExists) {
                    viewModel.transitionState = TRANSITION_END
                    viewBinding.root.transitionToEnd()
                } else {
                    viewModel.transitionState = TRANSITION_START
                    viewBinding.root.transitionToStart()
                }
            }
        }
    }

    private suspend fun observeSearchTextChanges() {
        viewModel.searchTextChanged.collect { searchText ->
            withContext(Dispatchers.Main) {
                viewBinding.editTextSearch.setText(searchText)
                viewBinding.editTextSearch.setSelection(searchText.length)
            }
        }
    }

    private fun provideItemDecoration(): SearchItemDecoration {
        return requireContext().let {
            if (it.isLandscapeOrientation) {
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
        if (!viewBinding.root.isAtEnd) {
            viewModel.onBackClick()
        } else {
            viewBinding.root.addTransitionListener(object : MotionManager() {
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    viewModel.onBackClick()
                }
            })
            viewBinding.root.transitionToStart()
        }
    }

    private fun hideCloseIcon() {
        if (!viewBinding.imageViewIconClose.isOut) {

            if (scaleOut == null) {
                scaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    viewBinding.imageViewIconClose.isOut = true
                }
            }

            scaleOut!!.setAnimationListener(callback)
            viewBinding.imageViewIconClose.startAnimation(scaleOut!!)
        }
    }

    private fun showCloseIcon() {
        if (viewBinding.imageViewIconClose.isOut) {

            if (scaleIn == null) {
                scaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding.imageViewIconClose.isOut = false
                }
            }

            scaleIn!!.setAnimationListener(callback)
            viewBinding.imageViewIconClose.startAnimation(scaleIn!!)
        }
    }

    private fun onCloseIconClick() {
        viewBinding.editTextSearch.setText("")
    }

    companion object {
        const val TRANSITION_START = 0f
        const val TRANSITION_END = 1f

        fun newInstance(data: Any?): SearchFragment {
            return SearchFragment()
        }
    }
}