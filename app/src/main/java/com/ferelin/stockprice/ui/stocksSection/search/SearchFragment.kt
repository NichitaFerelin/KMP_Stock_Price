package com.ferelin.stockprice.ui.stocksSection.search

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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment :
    BaseStocksFragment<FragmentSearchBinding, SearchViewModel, SearchViewController>() {

    override val mViewController: SearchViewController = SearchViewController()
    override val mViewModel: SearchViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewController.setArgumentsViewDependsOn(
            stocksRecyclerAdapter = mViewModel.stocksRecyclerAdapter,
            searchesHistoryRecyclerAdapter = mViewModel.searchRequestAdapter,
            popularSearchesRecyclerAdapter = mViewModel.popularRequestsAdapter,
            savedViewTransitionState = mViewModel.savedViewTransitionState,
            fragmentManager = parentFragmentManager
        )

        setUpBackPressedCallback()
        setUpClickListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateSearchRequests() }
            launch { collectStatePopularSearchRequests() }
            launch { collectStateSearchResults() }
            launch { collectEventOnError() }
        }
    }

    override fun onStop() {
        super.onStop()
        mViewController.onStop()
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding!!.imageViewBack.setOnClickListener {
            mViewController.onBackButtonClicked(
                ifNotHandled = {
                    mOnBackPressedCallback.remove()
                    activity?.onBackPressed()
                }
            )
        }
        mViewController.viewBinding!!.editTextSearch.doAfterTextChanged {
            mViewModel.onSearchTextChanged(it?.toString() ?: "")
            mViewController.onSearchTextChanged(it?.toString() ?: "")
        }
        mViewController.viewBinding!!.imageViewIconClose.setOnClickListener {
            mViewController.onCloseIconClicked()
        }
    }

    private suspend fun collectStateSearchResults() {
        mViewModel.stateSearchStockResults.collect { results ->
            withContext(mCoroutineContext.Main) {
                val transitionState = mViewController.onSearchStocksResultChanged(results)
                mViewModel.savedViewTransitionState = transitionState
            }
        }
    }

    private suspend fun collectStateSearchRequests() {
        mViewModel.stateSearchRequests
            .filter { it !is DataNotificator.Loading && it != null }
            .collect { notificator ->
                withContext(mCoroutineContext.Main) {
                    mViewController.onSearchRequestsChanged(notificator!!)
                }
            }
    }

    private suspend fun collectStatePopularSearchRequests() {
        mViewModel.statePopularSearchRequests
            .filter { it != null }
            .take(1)
            .collect { results ->
                withContext(mCoroutineContext.Main) {
                    mViewController.onPopularSearchRequestsChanged(results!!)
                }
            }
    }

    private suspend fun collectEventOnError() {
        mViewModel.eventOnError.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onError(it)
            }
        }
    }

    private val mOnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!mViewController.handleOnBackPressed(mViewModel.searchRequest)) {
                this.remove()
                activity?.onBackPressed()
            }
        }
    }

    private fun setUpBackPressedCallback() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            mOnBackPressedCallback
        )
    }
}