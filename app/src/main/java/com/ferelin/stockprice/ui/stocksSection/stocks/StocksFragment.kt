package com.ferelin.stockprice.ui.stocksSection.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentStocksBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.AnimationManager
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksFragment : BaseStocksFragment<StocksViewModel, StocksViewHelper>() {

    override val mViewHelper: StocksViewHelper = StocksViewHelper()
    override val mViewModel: StocksViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    private lateinit var mBinding: FragmentStocksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager = requireParentFragment().parentFragmentManager
            mBinding.recyclerViewStocks.apply {
                adapter = mViewModel.recyclerAdapter
                addItemDecoration(StocksItemDecoration(requireContext()))
                setHasFixedSize(true)
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                (requireParentFragment() as StocksPagerFragment).eventOnFabClicked.collect {
                    withContext(mCoroutineContext.Main) {
                        scrollToTop()
                    }
                }
            }
            launch {
                mViewModel.actionShowError.collect {
                    withContext(mCoroutineContext.Main) {
                        showToast(it)
                    }
                }
            }
        }
    }

    private fun scrollToTop() {
        if ((mBinding.recyclerViewStocks.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding.recyclerViewStocks.visibility = View.VISIBLE
                    mBinding.recyclerViewStocks.smoothScrollToPosition(0)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding.recyclerViewStocks.smoothScrollBy(
                        0,
                        -mBinding.recyclerViewStocks.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mBinding.recyclerViewStocks.visibility = View.GONE
                    mBinding.recyclerViewStocks.scrollToPosition(20)
                    mViewHelper.runFadeIn(mBinding.recyclerViewStocks, fadeInCallback)
                }
            }
            mViewHelper.runFadeOut(mBinding.recyclerViewStocks, fadeOutCallback)
        } else mBinding.recyclerViewStocks.smoothScrollToPosition(0)
    }
}