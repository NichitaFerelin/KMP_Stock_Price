package com.ferelin.stockprice.ui.stocksSection.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@FlowPreview
class FavouriteFragment : BaseStocksFragment<FavouriteViewModel, FavouriteViewHelper>() {

    override val mViewHelper: FavouriteViewHelper = FavouriteViewHelper()
    override val mViewModel: FavouriteViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    override var mStocksRecyclerView: RecyclerView? = null
    private var mBinding: FragmentFavouriteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false).also {
            mStocksRecyclerView = it.recyclerViewFavourites
        }
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mFragmentManager = requireParentFragment().parentFragmentManager
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.actionScrollToTop.collect {
                withContext(mCoroutineContext.Main) {
                    hardScrollToTop()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.postponeReferencesRemove {
            mBinding?.recyclerViewFavourites?.adapter = null
            mBinding = null
        }
    }

    private fun hardScrollToTop() {
        mBinding!!.recyclerViewFavourites.scrollToPosition(0)
    }
}
