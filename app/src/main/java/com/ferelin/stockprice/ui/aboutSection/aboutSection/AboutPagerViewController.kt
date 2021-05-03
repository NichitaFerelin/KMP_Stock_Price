package com.ferelin.stockprice.ui.aboutSection.aboutSection

import android.graphics.Color
import android.os.Bundle
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.custom.OrderedTextView
import com.ferelin.stockprice.databinding.FragmentAboutPagerBinding
import com.ferelin.stockprice.utils.showToast
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform

class AboutPagerViewController :
    BaseViewController<AboutPagerViewAnimator, FragmentAboutPagerBinding>() {

    override val mViewAnimator: AboutPagerViewAnimator = AboutPagerViewAnimator()

    private var mSelectedTabPagePosition = 0
    private val mViewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val selectedTab = getTabByPosition(position)
            onTabClicked(selectedTab)
        }
    }

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        setUpTransitions(fragment)
    }

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        postponeTransitions(fragment)
        setUpViewPager()
    }

    override fun onDestroyView() {
        viewBinding!!.viewPager.unregisterOnPageChangeCallback(mViewPagerCallback)
        super.onDestroyView()
    }

    fun setArgumentsViewDependsOn(
        viewPagerAdapter: AboutPagerAdapter,
        lastSelectedTabPosition: Int
    ) {
        viewBinding!!.viewPager.adapter = viewPagerAdapter
        restoreSelectedTab(lastSelectedTabPosition)
    }

    fun onTabClicked(selectedTab: OrderedTextView) {
        if (isTabAlreadySelected(mSelectedTabPagePosition, selectedTab.orderNumber)) {
            return
        }

        val previousTab = getTabByPosition(mSelectedTabPagePosition)
        changeTabStyle(previousTab, selectedTab)
        viewBinding!!.viewPager.currentItem = selectedTab.orderNumber
        mSelectedTabPagePosition = selectedTab.orderNumber
    }

    fun onFavouriteIconClicked() {
        mViewAnimator.runScaleInOut(viewBinding!!.imageViewStar)
    }

    fun onDataChanged(companyName: String, companySymbol: String, favouriteIconResource: Int) {
        viewBinding!!.textViewCompanyName.text = companyName
        viewBinding!!.textViewCompanySymbol.text = companySymbol
        viewBinding!!.imageViewStar.setImageResource(favouriteIconResource)
    }

    fun onError(text: String) {
        mContext?.let { showToast(it, text) }
    }

    fun handleOnBackPressed(): Boolean {
        return if (isNotFirstPageSelected()) {
            viewBinding!!.viewPager.setCurrentItem(0, true)
            true
        } else false
    }

    private fun setUpViewPager() {
        viewBinding!!.viewPager.registerOnPageChangeCallback(mViewPagerCallback)
        viewBinding!!.viewPager.offscreenPageLimit = 5
    }

    private fun changeTabStyle(previousTab: OrderedTextView, newTab: OrderedTextView) {
        TextViewCompat.setTextAppearance(previousTab, R.style.textViewBodyShadowedTab)
        TextViewCompat.setTextAppearance(newTab, R.style.textViewH3Tab)
        mViewAnimator.runScaleInOut(newTab)
    }

    private fun restoreSelectedTab(lastSelectedTabPosition: Int) {
        mSelectedTabPagePosition = lastSelectedTabPosition
        if (isCorrectTabSelected(lastSelectedTabPosition, mSelectedTabPagePosition)) {
            val newTab = getTabByPosition(mSelectedTabPagePosition)
            changeTabStyle(viewBinding!!.textViewChart, newTab)
        }
    }

    private fun getTabByPosition(position: Int): OrderedTextView {
        return when (position) {
            0 -> viewBinding!!.textViewChart
            1 -> viewBinding!!.textViewSummary
            2 -> viewBinding!!.textViewNews
            3 -> viewBinding!!.textViewForecasts
            4 -> viewBinding!!.textViewIdeas
            else -> throw IllegalStateException("Unexpected tab position: $position")
        }
    }

    private fun setUpTransitions(fragment: Fragment) {
        fragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
        fragment.exitTransition = Hold()
    }

    private fun isCorrectTabSelected(selectedPosition: Int, mustBeSelected: Int): Boolean {
        return selectedPosition == mustBeSelected
    }

    private fun isTabAlreadySelected(current: Int, new: Int): Boolean {
        return current == new
    }

    private fun isNotFirstPageSelected(): Boolean {
        return mSelectedTabPagePosition != 0
    }
}