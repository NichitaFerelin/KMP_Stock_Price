package com.ferelin.stockprice.ui.stocksSection.stocksPager

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

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentStocksPagerBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.onSlide.AlphaSlideAction
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.onSlide.ArrowUpAction
import com.ferelin.stockprice.ui.stocksSection.favourite.FavouriteFragment
import com.ferelin.stockprice.ui.stocksSection.stocks.StocksFragment
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.bottomDrawer.BottomSheetManager
import com.ferelin.stockprice.utils.isHidden
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.transition.Hold

class StocksPagerViewController :
    BaseViewController<StocksPagerViewAnimator, FragmentStocksPagerBinding>() {

    override val mViewAnimator: StocksPagerViewAnimator = StocksPagerViewAnimator()

    private lateinit var mViewPagerChangeCallback: ViewPager2.OnPageChangeCallback

    private var mBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private val mBottomSheetManager: BottomSheetManager by lazy(LazyThreadSafetyMode.NONE) {
        BottomSheetManager()
    }

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        fragment.exitTransition = Hold()
        fragment.enterTransition = Fade(Fade.IN)
    }

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        postponeTransitions(fragment)
        setUpViewPager()
        initBottomDrawer()
    }

    override fun onDestroyView() {
        mBottomSheetBehavior = null
        viewBinding.viewPager.unregisterOnPageChangeCallback(mViewPagerChangeCallback)
        super.onDestroyView()
    }

    fun setUpArgumentsViewDependsOn(
        viewPagerAdapter: StocksPagerAdapter,
        arrowState: Float,
        scrimVisibilityState: Int
    ) {
        viewBinding.viewPager.adapter = viewPagerAdapter
        viewBinding.bottomAppBarImageViewArrowUp.rotation = arrowState
        restoreScrimState(scrimVisibilityState)
    }

    fun onControlButtonPressed() {
        if (mBottomSheetBehavior!!.isHidden()) {
            openDrawer()
        } else closeDrawer()
    }

    fun onScrimClicked() {
        closeDrawer()
    }

    fun onFabClicked(currentFragment: StocksPagerFragment) {
        val childFragments = currentFragment.childFragmentManager.fragments

        // BottomDrawerFragment is first at list in fragment manager
        val currentChildPosition = viewBinding.viewPager.currentItem + 1

        when (val currentChildFragment = childFragments.getOrNull(currentChildPosition)) {
            null -> return
            is StocksFragment -> currentChildFragment.onFabClicked()
            is FavouriteFragment -> currentChildFragment.onFabClicked()
        }

        hideFab()
    }

    fun onHintStocksClicked() {
        if (viewBinding.viewPager.currentItem != 0) {
            viewBinding.viewPager.setCurrentItem(0, true)
        }
    }

    fun onHintFavouriteClicked() {
        if (viewBinding.viewPager.currentItem != 1) {
            viewBinding.viewPager.setCurrentItem(1, true)
        }
    }

    fun onCardSearchClicked(fragment: Fragment) {
        navigateToSearchFragment(fragment)
    }

    fun handleOnBackPressed(): Boolean {
        return when {
            viewBinding.viewPager.currentItem == 0 -> false
            !mBottomSheetBehavior!!.isHidden() -> {
                closeDrawer()
                true
            }
            else -> {
                viewBinding.viewPager.setCurrentItem(0, true)
                true
            }
        }
    }

    private fun initBottomDrawer() {
        mBottomSheetBehavior = BottomSheetBehavior.from(viewBinding.containerRoot)
            .also {
                it.addBottomSheetCallback(mBottomSheetManager)
                it.state = BottomSheetBehavior.STATE_HIDDEN
            }

        mBottomSheetManager.apply {
            addOnSlideAction(AlphaSlideAction(viewBinding.viewScrim))
            addOnSlideAction(ArrowUpAction(viewBinding.bottomAppBarImageViewArrowUp))
        }
    }

    private fun restoreScrimState(scrimVisibilityState: Int) {
        if (scrimVisibilityState != View.GONE) {
            viewBinding.viewScrim.visibility = scrimVisibilityState
            viewBinding.viewScrim.alpha = 1F
        }
    }

    private fun setUpViewPager() {
        mViewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                switchTextStyles(position)
            }
        }
        viewBinding.viewPager.registerOnPageChangeCallback(mViewPagerChangeCallback)
    }

    private fun hideFab() {
        mViewAnimator.runScaleOut(viewBinding.fab, object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding.fab.visibility = View.INVISIBLE
                viewBinding.fab.scaleX = 1.0F
                viewBinding.fab.scaleY = 1.0F
            }
        })
    }

    private fun openDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun navigateToSearchFragment(fragment: Fragment) {
        Navigator.navigateToSearchFragment(fragment) {
            it.addSharedElement(
                viewBinding.toolbar,
                context.resources.getString(R.string.transitionSearchFragment)
            )
        }
    }

    private fun applyDefaultStyle(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH2Shadowed)
    }

    private fun applySelectedStyle(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH1)
    }

    private fun switchTextStyles(selectedPosition: Int) {
        if (selectedPosition == 0) {
            applySelectedStyle(viewBinding.textViewHintStocks)
            applyDefaultStyle(viewBinding.textViewHintFavourite)
            mViewAnimator.runScaleInOut(viewBinding.textViewHintStocks)
        } else {
            applySelectedStyle(viewBinding.textViewHintFavourite)
            applyDefaultStyle(viewBinding.textViewHintStocks)
            mViewAnimator.runScaleInOut(viewBinding.textViewHintFavourite)
        }
    }
}