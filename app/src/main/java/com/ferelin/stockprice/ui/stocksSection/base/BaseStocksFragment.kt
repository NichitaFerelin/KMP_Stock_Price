package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.ui.aboutSection.aboutSection.AboutPagerFragment
import com.ferelin.stockprice.ui.stocksSection.common.StockClickListener
import com.ferelin.stockprice.ui.stocksSection.common.StockItemAnimator
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import com.ferelin.stockprice.ui.stocksSection.common.StocksItemDecoration
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.swipe.SwipeActionCallback
import com.google.android.material.transition.Hold
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStocksFragment<out T : BaseStocksViewModel, out V : BaseStocksViewHelper>
    : BaseFragment<T, V>(), StockClickListener {

    protected abstract var mStocksRecyclerView: RecyclerView?

    protected var mFragmentManager: FragmentManager? = null

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        mViewModel.recyclerAdapter.setOnStockCLickListener(this)
        mStocksRecyclerView!!.apply {
            itemAnimator = StockItemAnimator()
            ItemTouchHelper(SwipeActionCallback()).attachToRecyclerView(this)
            adapter = mViewModel.recyclerAdapter
            addItemDecoration(StocksItemDecoration(requireContext()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeTransition(view)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            if (parentFragment is StocksPagerFragment) {
                (parentFragment as StocksPagerFragment).eventOnFabClicked.collect {
                    withContext(mCoroutineContext.Main) {
                        scrollToTopWithAnimation()
                    }
                }
            }
        }
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onStockClicked(
        stockViewHolder: StockViewHolder,
        company: AdaptiveCompany
    ) {
        moveToAboutFragment(stockViewHolder, company)
    }

    override fun onHolderRebound(stockViewHolder: StockViewHolder) {
        startStarAnimation(stockViewHolder)
    }

    override fun onHolderUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean) {
        if (rebounded) {
            viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                val company = findCompanyByHolder(stockViewHolder)
                mViewModel.onFavouriteIconClicked(company)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mStocksRecyclerView = null
        mFragmentManager = null
        mViewModel.recyclerAdapter.removeListeners()
    }

    private fun startStarAnimation(holder: StockViewHolder) {
        val animationCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                val newImageResource =
                    if (holder.company!!.companyStyle.favouriteSingleIconResource == R.drawable.ic_star) {
                        R.drawable.ic_star_active
                    } else R.drawable.ic_star
                holder.binding.imageViewBoundedIcon.setImageResource(newImageResource)
                mViewHelper.runScaleOutLarge(holder.binding.imageViewBoundedIcon)
            }
        }
        mViewHelper.runScaleInLarge(holder.binding.imageViewBoundedIcon, animationCallback)
    }

    private fun findCompanyByHolder(stockViewHolder: StockViewHolder): AdaptiveCompany {
        val layoutManager = mStocksRecyclerView!!.layoutManager as LinearLayoutManager
        val viewHolderPosition = layoutManager.getPosition(stockViewHolder.itemView)
        return mViewModel.recyclerAdapter.getCompanyByAdapterPosition(viewHolderPosition)
    }

    private fun moveToAboutFragment(
        holder: StockViewHolder,
        company: AdaptiveCompany
    ) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mFragmentManager?.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, AboutPagerFragment(company))
                addToBackStack(null)
                addSharedElement(
                    holder.binding.root,
                    resources.getString(R.string.transitionAboutPager)
                )
            }
        }
    }

    private fun scrollToTopWithAnimation() {
        mStocksRecyclerView?.let { recyclerView ->
            if ((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 40) {
                val fadeInCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.smoothScrollToPosition(0)
                    }
                }
                val fadeOutCallback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        recyclerView.smoothScrollBy(
                            0,
                            -recyclerView.height
                        )
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        recyclerView.visibility = View.GONE
                        recyclerView.scrollToPosition(20)
                        mViewHelper.runFadeIn(recyclerView, fadeInCallback)
                    }
                }
                mViewHelper.runFadeOut(recyclerView, fadeOutCallback)
            } else recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun postponeTransition(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}