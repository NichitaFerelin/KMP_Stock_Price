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
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteFragment :
    BaseStocksFragment<FragmentFavouriteBinding, FavouriteViewModel, FavouriteViewController>() {

    override val mViewController: FavouriteViewController = FavouriteViewController()
    override val mViewModel: FavouriteViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        mViewController.viewBinding = viewBinding
        return viewBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewController.setArgumentsViewDependsOn(
            stocksRecyclerAdapter = mViewModel.stocksRecyclerAdapter,
            fragmentManager = requireParentFragment().parentFragmentManager
        )
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectEventOnNewItem()
        }
    }

    private suspend fun collectEventOnNewItem() {
        mViewModel.eventOnNewItem.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onNewItem()
            }
        }
    }
}
