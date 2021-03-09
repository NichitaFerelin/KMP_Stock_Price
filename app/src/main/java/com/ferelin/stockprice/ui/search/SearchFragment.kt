package com.ferelin.stockprice.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseStocksFragment
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.common.StocksItemDecoration
import com.ferelin.stockprice.utils.CoroutineContextProvider
import com.ferelin.stockprice.utils.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        with(mBinding) {
            recyclerViewSearchResults.apply {
                addItemDecoration(StocksItemDecoration(requireContext()))
                adapter = mViewModel.recyclerAdapter
            }
            recyclerViewSearchedHistory.apply {
                adapter = mViewModel.searchesAdapter
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            recyclerViewPopularRequests.apply {
                adapter = mViewModel.popularRequestsAdapter
                addItemDecoration(SearchItemDecoration(requireContext()))
            }
            editTextSearch.addTextChangedListener {
                mViewModel.onSearchTextChanged(it)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        lifecycleScope.launch(mCoroutineContext.IO) {
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
        }
    }
}