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

package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.ui.stocksSection.common.StockItemAnimator
import com.ferelin.stockprice.ui.stocksSection.common.StockItemDecoration
import com.ferelin.stockprice.ui.stocksSection.common.adapter.StockViewHolder
import com.ferelin.stockprice.ui.stocksSection.common.adapter.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.swipe.SwipeActionCallback
import com.ferelin.stockprice.utils.withTimer

abstract class BaseStocksViewController<ViewBindingType : ViewBinding> :
    BaseViewController<BaseStocksViewAnimator, ViewBindingType>() {

    abstract val stocksRecyclerView: RecyclerView

    protected val mStocksRecyclerAdapter: StocksRecyclerAdapter?
        get() = if (stocksRecyclerView.adapter is StocksRecyclerAdapter) {
            stocksRecyclerView.adapter as StocksRecyclerAdapter
        } else null

    /*
    * To use common "replace fragment" logic at different fragments
    * */
    var fragmentManager: FragmentManager? = null

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        postponeTransitions(fragment)
        setUpStocksRecyclerView()
    }

    override fun onDestroyView() {
        fragmentManager = null
        super.onDestroyView()
    }

    fun onStockHolderRebound(stockViewHolder: StockViewHolder) {
        startStarAnimation(stockViewHolder)
    }

    fun onStockHolderUntouched(
        stockViewHolder: StockViewHolder,
        rebounded: Boolean,
        onAccepted: (AdaptiveCompany) -> Unit
    ) {
        if (rebounded) {
            val company = findCompanyByHolder(stockViewHolder)
            onAccepted.invoke(company)
        }
    }

    fun onStockClicked(company: AdaptiveCompany) {
        mNavigator?.navigateToAboutPagerFragment(company, fragmentManager!!)
    }

    fun onFabClicked() {
        scrollToTopWithAnimation()
    }

    fun onCompanyChanged(notificator: DataNotificator<AdaptiveCompany>) {
        val company = notificator.data!!
        mStocksRecyclerAdapter?.companies?.indexOf(company)?.let { actualCompanyIndex ->
            if (actualCompanyIndex != NULL_INDEX) {
                mStocksRecyclerAdapter?.notifyUpdated(actualCompanyIndex)
            }
        }
    }

    private fun setUpStocksRecyclerView() {
        stocksRecyclerView.apply {
            itemAnimator = StockItemAnimator()
            ItemTouchHelper(SwipeActionCallback()).attachToRecyclerView(this)
            addItemDecoration(StockItemDecoration(context))
        }
    }

    private fun startStarAnimation(holder: StockViewHolder) {
        val animationCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                val newImageResource =
                    if (holder.company!!.companyStyle.favouriteForegroundIconResource == R.drawable.ic_star) {
                        R.drawable.ic_star_active
                    } else R.drawable.ic_star
                holder.binding.imageViewBoundedIcon.setImageResource(newImageResource)
                mViewAnimator.runScaleOutLarge(holder.binding.imageViewBoundedIcon)
            }
        }
        mViewAnimator.runScaleInLarge(holder.binding.imageViewBoundedIcon, animationCallback)
    }

    private fun findCompanyByHolder(stockViewHolder: StockViewHolder): AdaptiveCompany {
        val layoutManager = stocksRecyclerView.layoutManager as LinearLayoutManager
        val viewHolderPosition = layoutManager.getPosition(stockViewHolder.itemView)
        return mStocksRecyclerAdapter!!.getCompanyByAdapterPosition(viewHolderPosition)
    }

    private fun scrollToTopWithAnimation() {
        val layoutManager = stocksRecyclerView.layoutManager
        if (layoutManager is LinearLayoutManager && layoutManager.findFirstVisibleItemPosition() < 40) {
            stocksRecyclerView.smoothScrollToPosition(0)
            return
        }

        val fadeInCallback = object : AnimationManager() {
            override fun onAnimationStart(animation: Animation?) {
                stocksRecyclerView.alpha = 1F
                stocksRecyclerView.smoothScrollToPosition(0)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // To avoid graphic bug
                withTimer { stocksRecyclerView.itemAnimator = StockItemAnimator() }
            }
        }

        val fadeOutCallback = object : AnimationManager() {
            override fun onAnimationStart(animation: Animation?) {
                stocksRecyclerView.itemAnimator = null
                stocksRecyclerView.smoothScrollBy(
                    0,
                    -stocksRecyclerView.height
                )
            }

            override fun onAnimationEnd(animation: Animation?) {
                stocksRecyclerView.alpha = 0F
                stocksRecyclerView.scrollToPosition(20)
                mViewAnimator.runFadeInAnimation(stocksRecyclerView, fadeInCallback)
            }
        }
        mViewAnimator.runFadeOutAnimation(stocksRecyclerView, fadeOutCallback)
    }
}