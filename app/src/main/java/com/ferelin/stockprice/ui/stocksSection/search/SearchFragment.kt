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

package com.ferelin.stockprice.ui.stocksSection.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment :
    BaseStocksFragment<FragmentSearchBinding, SearchViewModel, SearchViewController>() {

    override val mViewController = SearchViewController()
    override val mViewModel: SearchViewModel by viewModels()

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

        setUpClickListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateSearchResults() }
            launch { collectEventOnError() }
        }
    }

    override fun onStop() {
        super.onStop()
        mViewController.onStop()
    }

    override fun onBackPressedHandle(): Boolean {
        return mViewController.onBackSwiped(mViewModel.lastSearchRequest)
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding.imageViewBack.setOnClickListener {
            mViewController.onBackPressed()
        }
        mViewController.viewBinding.editTextSearch.doAfterTextChanged {
            mViewModel.onSearchTextChanged(it?.toString() ?: "")
            mViewController.onSearchTextChanged(it?.toString() ?: "")
        }
        mViewController.viewBinding.imageViewIconClose.setOnClickListener {
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

    private suspend fun collectEventOnError() {
        mViewModel.eventOnError.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onError(it)
            }
        }
    }
}