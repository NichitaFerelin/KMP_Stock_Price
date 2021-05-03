package com.ferelin.stockprice.ui.stocksSection.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

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
            .filter { it != null }
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