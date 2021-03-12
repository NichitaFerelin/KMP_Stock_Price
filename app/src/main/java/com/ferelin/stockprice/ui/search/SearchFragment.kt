package com.ferelin.stockprice.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseStocksFragment
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.utils.DataViewModelFactory

class SearchFragment(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : BaseStocksFragment<SearchViewModel>() {

    private lateinit var mBinding: FragmentSearchBinding

    override val mViewModel: SearchViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpComponents() {
        super.setUpComponents()
        mFragmentManager = parentFragmentManager
/*
        with(mBinding) {
            recyclerViewSearchResults.apply {
                addItemDecoration(StocksItemDecoration(requireContext()))
                adapter = mViewModel.recyclerAdapter
            }
            recyclerViewSearchedHistory.apply {
                adapter = mViewModel.searchesAdapter.also {
                    it.setOnTickerClickListener { item, _ ->
                        onSearchTickerClicked(item)
                    }
                }
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            recyclerViewPopularRequests.apply {
                adapter = mViewModel.popularRequestsAdapter.also {
                    it.setPopularSearches()
                    it.setOnTickerClickListener { item, _ ->
                        onSearchTickerClicked(item)
                    }
                }
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            editTextSearch.addTextChangedListener {
                mViewModel.onSearchTextChanged(it)
            }
        }*/
    }

    override fun initObservers() {
        super.initObservers()

       /* lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionShowHintsHideResults.collect {
                withContext(mCoroutineContext.Main) {
                    if (it) {
                        mBinding.sectionResults.visibility = View.GONE
                        mBinding.sectionHints.visibility = View.VISIBLE
                    } else {
                        mBinding.sectionHints.visibility = View.GONE
                        mBinding.sectionResults.visibility = View.VISIBLE
                    }
                }
            }
        }*/
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        mBinding.editTextSearch.setText(item.searchText)
    }
}