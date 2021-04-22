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
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StocksFragment : BaseStocksFragment<StocksViewModel, StocksViewHelper>() {

    override val mViewHelper: StocksViewHelper = StocksViewHelper()
    override val mViewModel: StocksViewModel by viewModels {
        DataViewModelFactory(CoroutineContextProvider(), mDataInteractor)
    }

    private var mBinding: FragmentStocksBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStocksBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        mFragmentManager = requireParentFragment().parentFragmentManager
        mBinding!!.recyclerViewStocks.apply {
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
            setHasFixedSize(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.postponeReferenceRemoving {
            mBinding?.recyclerViewStocks?.adapter = null
            mBinding = null
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
        mBinding!!.apply {
            if ((recyclerViewStocks.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
                val fadeInCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerViewStocks.visibility = View.VISIBLE
                        recyclerViewStocks.smoothScrollToPosition(0)
                    }
                }
                val fadeOutCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerViewStocks.smoothScrollBy(
                            0,
                            -recyclerViewStocks.height
                        )
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        recyclerViewStocks.visibility = View.GONE
                        recyclerViewStocks.scrollToPosition(20)
                        mViewHelper.runFadeIn(recyclerViewStocks, fadeInCallback)
                    }
                }
                mViewHelper.runFadeOut(recyclerViewStocks, fadeOutCallback)
            } else recyclerViewStocks.smoothScrollToPosition(0)
        }
    }
}