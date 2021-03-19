package com.ferelin.stockprice.ui.stocksSection.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteFragment : BaseStocksFragment<FavouriteViewModel>() {

    private lateinit var mBinding: FragmentFavouriteBinding

    override val mViewModel: FavouriteViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents() {
        super.setUpViewComponents()

        mFragmentManager = requireParentFragment().parentFragmentManager

        mBinding.recyclerViewFavourites.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionScrollToTop.collect {
                    withContext(mCoroutineContext.Main) {
                        scrollToTop()
                    }
                }
            }
            launch {
                (requireParentFragment() as StocksPagerFragment).eventOnFabClicked.collect {
                    withContext(mCoroutineContext.Main) {
                        scrollToTop()
                    }
                }
            }
        }
    }

    private fun scrollToTop() {
        mBinding.recyclerViewFavourites.scrollToPosition(0)
    }
}