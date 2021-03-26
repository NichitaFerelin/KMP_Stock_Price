package com.ferelin.stockprice.ui.stocksSection.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.AnimationManager
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

    private lateinit var mBinding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager = requireParentFragment().parentFragmentManager
            mBinding.recyclerViewFavourites.apply {
                adapter = mViewModel.recyclerAdapter
                addItemDecoration(StocksItemDecoration(requireContext()))
            }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionScrollToTop.collect {
                    withContext(mCoroutineContext.Main) {
                        hardScrollToTop()
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
        if ((mBinding.recyclerViewFavourites.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding.recyclerViewFavourites.visibility = View.VISIBLE
                    mBinding.recyclerViewFavourites.smoothScrollToPosition(0)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding.recyclerViewFavourites.smoothScrollBy(
                        0,
                        -mBinding.recyclerViewFavourites.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mBinding.recyclerViewFavourites.visibility = View.GONE
                    mBinding.recyclerViewFavourites.scrollToPosition(20)
                    mViewHelper.runFadeIn(mBinding.recyclerViewFavourites, fadeInCallback)
                }
            }
            mViewHelper.runFadeOut(mBinding.recyclerViewFavourites, fadeOutCallback)
        } else mBinding.recyclerViewFavourites.smoothScrollToPosition(0)
    }

    private fun hardScrollToTop() {
        mBinding.recyclerViewFavourites.scrollToPosition(0)
    }
}
