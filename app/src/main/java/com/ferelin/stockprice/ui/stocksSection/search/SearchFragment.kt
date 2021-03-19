package com.ferelin.stockprice.ui.stocksSection.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.utils.showSnackbar
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class SearchFragment : BaseStocksFragment<SearchViewModel>() {

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

    override fun setUpViewComponents() {
        super.setUpViewComponents()
        mFragmentManager = parentFragmentManager

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
            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    Timer().apply {
                        schedule(timerTask {
                            mViewModel.onSearchTextChanged(s)
                        }, 150)
                    }
                }
            })


            imageViewIconClose.setOnClickListener {
                mBinding.editTextSearch.setText("")
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
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
            launch {
                mViewModel.actionHideCloseIcon.collect {
                    withContext(mCoroutineContext.Main) {
                        if (it && mBinding.imageViewIconClose.visibility != View.GONE) {
                            mBinding.imageViewIconClose.visibility = View.GONE
                        } else if (!it && mBinding.imageViewIconClose.visibility != View.VISIBLE) {
                            mBinding.imageViewIconClose.visibility = View.VISIBLE
                        }
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showSnackbar(mBinding.root, it)
                    }
                }
            }
        }
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        mBinding.editTextSearch.setText(item.searchText)
    }
}