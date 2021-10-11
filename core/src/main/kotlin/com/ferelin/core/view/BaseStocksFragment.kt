/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.core.view

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ferelin.core.R
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.adapter.base.ifLinear
import com.ferelin.core.adapter.base.scrollToTopWithCustomAnim
import com.ferelin.core.adapter.stocks.StockItemAnimator
import com.ferelin.core.adapter.stocks.StockItemDecoration
import com.ferelin.core.adapter.stocks.StockViewHolder
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.swipe.SwipeActionCallback
import com.ferelin.core.viewData.StockViewData
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.core.viewModel.StocksMode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class BaseStocksFragment<VB : ViewBinding, VM : BaseStocksViewModel> : BaseFragment<VB>() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<VM>

    @Inject
    lateinit var mStockStyleProvider: StockStyleProvider

    abstract val mViewModel: VM
    abstract val mStocksMode: StocksMode

    protected var stocksRecyclerView: RecyclerView? = null

    private var mScaleIn: Animation? = null
    private var mScaleOut: Animation? = null
    private var mFadeOut: Animation? = null
    private var mFadeIn: Animation? = null

    override fun initUi() {
        stocksRecyclerView?.apply {
            adapter = mViewModel.stocksAdapter
            itemAnimator = StockItemAnimator()
            addItemDecoration(StockItemDecoration(requireContext()))

            ItemTouchHelper(
                SwipeActionCallback(
                    onHolderRebound = this@BaseStocksFragment::onHolderRebound,
                    onHolderUntouched = this@BaseStocksFragment::onHolderUntouched
                )
            ).attachToRecyclerView(this)
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            withContext(mDispatchersProvider.IO) {
                launch { mViewModel.loadStocks(mStocksMode) }
                launch { mViewModel.companiesStockPriceUpdates.collect() }
                launch { mViewModel.favouriteCompaniesUpdates.collect() }
                launch { mViewModel.actualStockPrice.collect() }
            }
        }
    }

    override fun onDestroyView() {
        invalidateAnims()
        stocksRecyclerView = null
        super.onDestroyView()
    }

    fun onFabClick() {
        stocksRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager.ifLinear { layoutManager ->

                if (layoutManager.findFirstVisibleItemPosition() < sScrollWithAnimAfter) {
                    recyclerView.smoothScrollToPosition(0)
                } else {
                    initFadeAnims()
                    recyclerView.scrollToTopWithCustomAnim(
                        mFadeIn!!,
                        mFadeOut!!,
                        StockItemAnimator()
                    )
                }
            }
        }
    }

    private fun onHolderRebound(stockViewHolder: StockViewHolder) {
        animateStarJump(stockViewHolder)
    }

    private fun onHolderUntouched(stockViewHolder: StockViewHolder, isRebounded: Boolean) {
        if (isRebounded) {
            mViewModel.onHolderUntouched(stockViewHolder)
        }
    }

    private fun animateStarJump(stockViewHolder: StockViewHolder) {
        initScaleAnims()

        val scaleInCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                val starRes = mStockStyleProvider.getForegroundIconDrawable(false)
                val startActiveRes = mStockStyleProvider.getForegroundIconDrawable(true)

                val starDrawable = ContextCompat.getDrawable(requireContext(), starRes)
                val starActiveDrawable = ContextCompat.getDrawable(requireContext(), startActiveRes)

                getStockViewData(stockViewHolder.layoutPosition)?.let { stockViewData ->
                    with(stockViewHolder.viewBinding.imageViewBoundedIcon) {
                        if (stockViewData.isFavourite) {
                            setImageDrawable(starDrawable)
                        } else {
                            setImageDrawable(starActiveDrawable)
                        }

                        startAnimation(mScaleOut!!)
                    }
                }
            }
        }

        mScaleIn!!.setAnimationListener(scaleInCallback)
        stockViewHolder.viewBinding.imageViewBoundedIcon.startAnimation(mScaleIn!!)
    }

    private fun getStockViewData(position: Int): StockViewData? {
        return stocksRecyclerView?.adapter?.let { adapter ->
            return if (
                adapter is BaseRecyclerAdapter
                && adapter.getByPosition(position) is StockViewData
            ) {
                (adapter.getByPosition(position) as StockViewData)
            } else {
                null
            }
        }
    }

    private fun initFadeAnims() {
        if (mFadeIn == null) {
            mFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            mFadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }

    private fun initScaleAnims() {
        if (mScaleIn == null) {
            mScaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in_large)
            mScaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out_large)
        }
    }

    private fun invalidateAnims() {
        mFadeIn?.invalidate()
        mFadeOut?.invalidate()
        mScaleIn?.invalidate()
        mScaleOut?.invalidate()
    }

    companion object {
        private const val sScrollWithAnimAfter = 40
    }
}