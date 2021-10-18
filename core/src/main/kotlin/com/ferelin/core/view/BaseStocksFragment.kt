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

import android.os.Bundle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseStocksFragment<VB : ViewBinding, VM : BaseStocksViewModel> : BaseFragment<VB>() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<VM>

    @Inject
    lateinit var stockStyleProvider: StockStyleProvider

    abstract val viewModel: VM

    protected var stocksRecyclerView: RecyclerView? = null

    private var scaleIn: Animation? = null
    private var scaleOut: Animation? = null
    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    companion object {
        private const val SCROLL_WITH_ANIM_AFTER = 40
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            launch { viewModel.companiesStockPriceUpdates.collect() }
            launch { viewModel.favouriteCompaniesUpdates.collect() }
            launch { viewModel.actualStockPrice.collect() }
        }
    }

    override fun initUi() {
        stocksRecyclerView?.apply {
            adapter = viewModel.stocksAdapter
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

    override fun onDestroyView() {
        invalidateAnims()
        stocksRecyclerView = null
        super.onDestroyView()
    }

    fun onFabClick() {
        stocksRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager.ifLinear { layoutManager ->

                if (layoutManager.findFirstVisibleItemPosition() < SCROLL_WITH_ANIM_AFTER) {
                    recyclerView.smoothScrollToPosition(0)
                } else {
                    initFadeAnims()
                    recyclerView.scrollToTopWithCustomAnim(
                        fadeIn!!,
                        fadeOut!!,
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
            viewModel.onHolderUntouched(stockViewHolder)
        }
    }

    private fun animateStarJump(stockViewHolder: StockViewHolder) {
        initScaleAnims()

        val scaleInCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                val starRes = stockStyleProvider.getForegroundIconDrawable(false)
                val startActiveRes = stockStyleProvider.getForegroundIconDrawable(true)

                val starDrawable = ContextCompat.getDrawable(requireContext(), starRes)
                val starActiveDrawable = ContextCompat.getDrawable(requireContext(), startActiveRes)

                getStockViewDataBy(stockViewHolder.layoutPosition)?.let { stockViewData ->
                    with(stockViewHolder.viewBinding.imageViewBoundedIcon) {
                        if (stockViewData.isFavourite) {
                            setImageDrawable(starDrawable)
                        } else {
                            setImageDrawable(starActiveDrawable)
                        }

                        startAnimation(scaleOut!!)
                    }
                }
            }
        }

        scaleIn!!.setAnimationListener(scaleInCallback)
        stockViewHolder.viewBinding.imageViewBoundedIcon.startAnimation(scaleIn!!)
    }

    private fun getStockViewDataBy(position: Int): StockViewData? {
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
        if (fadeIn == null) {
            fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }

    private fun initScaleAnims() {
        if (scaleIn == null) {
            scaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in_large)
            scaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out_large)
        }
    }

    private fun invalidateAnims() {
        fadeIn?.invalidate()
        fadeOut?.invalidate()
        scaleIn?.invalidate()
        scaleOut?.invalidate()
    }
}