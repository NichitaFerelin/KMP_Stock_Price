package com.ferelin.stockprice.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.common.StocksAdapterType
import com.ferelin.stockprice.ui.common.StocksBaseFragment
import com.ferelin.stockprice.ui.common.StocksItemDecoration
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : StocksBaseFragment() {

    private val mSearchViewModel: SearchViewModel by viewModels()
    private lateinit var mBinding: FragmentSearchBinding

    override val mRecyclerAdapterType: StocksAdapterType
        get() = StocksAdapterType.Search

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

        with(mBinding) {
            recyclerViewSearchResults.apply {
                addItemDecoration(StocksItemDecoration(requireContext()))
                adapter = mStocksViewModel.getRecyclerAdapter(mRecyclerAdapterType).also {
                    it.setOnStocksCLickListener(this@SearchFragment)
                }
            }
            recyclerViewSearched.apply {
                adapter = mSearchViewModel.searchesAdapter
                addItemDecoration(SearchItemDecoration(requireContext()))
                mSearchViewModel.popularRequestsAdapter.setOnTickerClickListener { item, position ->

                }
            }
            recyclerViewPopularRequests.apply {
                adapter = mSearchViewModel.popularRequestsAdapter
                addItemDecoration(SearchItemDecoration(requireContext()))
                mSearchViewModel.popularRequestsAdapter.setOnTickerClickListener { item, position ->

                }
            }
            editTextSearch.addTextChangedListener {
                mSearchViewModel.onSearchTextChanged(it)
            }
        }
        mSearchViewModel.onSetUpComponents(mStocksViewModel.getRecyclerAdapter(mRecyclerAdapterType))
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(Dispatchers.IO) {
            launch {
                mDataInteractor.companiesState.collect {
                    mSearchViewModel.onCompaniesStateResponse(it)
                }
            }
            launch {
                mDataInteractor.popularRequestsState.collect {
                    mSearchViewModel.onPopularRequestsResponse(it)
                }
            }
            launch {
                mDataInteractor.searchedRequestsState.collect {
                    mSearchViewModel.onSearchesRequestsResponse(it)
                }
            }
            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    when (it) {
                        is DataNotificator.NewItem -> mRecyclerAdapter.updateCompany(it.data)
                        is DataNotificator.Remove -> mRecyclerAdapter.updateCompany(it.data)
                        else -> Unit
                    }
                }
            }
            launch {
                mSearchViewModel.sectionsVisibility.collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is SearchSectionVisibility.SHOW_HINTS_HIDE_RESULTS -> {
                                mBinding.sectionResults.visibility = View.GONE
                                mBinding.sectionHints.visibility = View.VISIBLE
                            }
                            is SearchSectionVisibility.HIDE_HINTS_SHOW_RESULTS -> {
                                mBinding.sectionHints.visibility = View.GONE
                                mBinding.sectionResults.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}