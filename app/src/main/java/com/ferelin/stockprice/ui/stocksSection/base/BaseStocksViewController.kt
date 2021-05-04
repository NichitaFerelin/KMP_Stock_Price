package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.stocksSection.common.StockItemAnimator
import com.ferelin.stockprice.ui.stocksSection.common.StockItemDecoration
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.NULL_INDEX
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.swipe.SwipeActionCallback
import com.google.android.material.transition.Hold
import kotlinx.coroutines.launch

abstract class BaseStocksViewController<ViewBinding> :
    BaseViewController<BaseStocksViewAnimator, ViewBinding>() {

    protected abstract val mStocksRecyclerView: RecyclerView

    private val mStocksRecyclerAdapter: StocksRecyclerAdapter
        get() = mStocksRecyclerView.adapter as StocksRecyclerAdapter

    /*
    * To use common "replace fragment" logic on different fragments
    * */
    var fragmentManager: FragmentManager? = null

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        fragment.exitTransition = Hold()
    }

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
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
            mViewLifecycleScope!!.launch(mCoroutineContext.IO) {
                val company = findCompanyByHolder(stockViewHolder)
                onAccepted.invoke(company)
            }
        }
    }

    fun onStockClicked(
        fragment: Fragment,
        stockViewHolder: StockViewHolder,
        company: AdaptiveCompany
    ) {
        navigateToAboutFragment(fragment, stockViewHolder, company)
    }

    fun onFabClicked() {
        scrollToTopWithAnimation()
    }

    fun onCompanyChanged(company: AdaptiveCompany) {
        val actualCompanyIndex = mStocksRecyclerAdapter.companies.indexOf(company)
        if (actualCompanyIndex != NULL_INDEX) {
            mStocksRecyclerAdapter.notifyUpdate(actualCompanyIndex)
        }
    }

    private fun setUpStocksRecyclerView() {
        mStocksRecyclerView.apply {
            itemAnimator = StockItemAnimator()
            ItemTouchHelper(SwipeActionCallback()).attachToRecyclerView(this)
            addItemDecoration(StockItemDecoration(mContext!!))
        }
    }

    private fun startStarAnimation(holder: StockViewHolder) {
        val animationCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                val newImageResource =
                    if (holder.company!!.companyStyle.favouriteSingleIconResource == R.drawable.ic_star) {
                        R.drawable.ic_star_active
                    } else R.drawable.ic_star
                holder.binding.imageViewBoundedIcon.setImageResource(newImageResource)
                mViewAnimator.runScaleOutLarge(holder.binding.imageViewBoundedIcon)
            }
        }
        mViewAnimator.runScaleInLarge(holder.binding.imageViewBoundedIcon, animationCallback)
    }

    private fun findCompanyByHolder(stockViewHolder: StockViewHolder): AdaptiveCompany {
        val layoutManager = mStocksRecyclerView.layoutManager as LinearLayoutManager
        val viewHolderPosition = layoutManager.getPosition(stockViewHolder.itemView)
        return mStocksRecyclerAdapter.getCompanyByAdapterPosition(viewHolderPosition)
    }

    private fun navigateToAboutFragment(
        fragment: Fragment,
        holder: StockViewHolder,
        company: AdaptiveCompany
    ) {
        Navigator.navigateToAboutPagerFragment(fragment, company, fragmentManager!!) {
            it.addSharedElement(
                holder.binding.root,
                mContext?.resources?.getString(R.string.transitionAboutPager) ?: ""
            )
        }
    }

    private fun scrollToTopWithAnimation() {
        if ((mStocksRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mStocksRecyclerView.visibility = View.VISIBLE
                    mStocksRecyclerView.smoothScrollToPosition(0)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mStocksRecyclerView.smoothScrollBy(
                        0,
                        -mStocksRecyclerView.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mStocksRecyclerView.visibility = View.GONE
                    mStocksRecyclerView.scrollToPosition(20)
                    mViewAnimator.runFadeInAnimation(mStocksRecyclerView, fadeInCallback)
                }
            }
            mViewAnimator.runFadeOutAnimation(mStocksRecyclerView, fadeOutCallback)
        } else mStocksRecyclerView.smoothScrollToPosition(0)
    }
}