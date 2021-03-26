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

    private var mBinding: FragmentFavouriteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        mFragmentManager = requireParentFragment().parentFragmentManager
        mBinding!!.recyclerViewFavourites.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
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

    override fun onDestroyView() {
        mBinding!!.recyclerViewFavourites.adapter = null
        mBinding = null
        super.onDestroyView()
    }

    private fun scrollToTop() {
        mBinding!!.apply {
            if ((recyclerViewFavourites.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
                val fadeInCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerViewFavourites.visibility = View.VISIBLE
                        recyclerViewFavourites.smoothScrollToPosition(0)
                    }
                }
                val fadeOutCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerViewFavourites.smoothScrollBy(
                            0,
                            -recyclerViewFavourites.height
                        )
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        recyclerViewFavourites.visibility = View.GONE
                        recyclerViewFavourites.scrollToPosition(20)
                        mViewHelper.runFadeIn(recyclerViewFavourites, fadeInCallback)
                    }
                }
                mViewHelper.runFadeOut(recyclerViewFavourites, fadeOutCallback)
            } else recyclerViewFavourites.smoothScrollToPosition(0)
        }
    }

    private fun hardScrollToTop() {
        mBinding!!.recyclerViewFavourites.scrollToPosition(0)
    }
}
