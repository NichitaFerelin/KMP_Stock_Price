package com.ferelin.stockprice.ui.aboutSection.aboutSection

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

import android.graphics.Color
import android.os.Bundle
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.custom.OrderedTextView
import com.ferelin.stockprice.databinding.FragmentAboutPagerBinding
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform

class AboutPagerViewController :
    BaseViewController<AboutPagerViewAnimator, FragmentAboutPagerBinding>() {

    override val mViewAnimator = AboutPagerViewAnimator()

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

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        postponeTransitions(fragment)
        setUpViewPager()
    }

    override fun onDestroyView() {
        viewBinding.viewPager.unregisterOnPageChangeCallback(mViewPagerCallback)
        super.onDestroyView()
    }

    fun setArgumentsViewDependsOn(
        viewPagerAdapter: AboutPagerAdapter,
        lastSelectedTabPosition: Int,
        selectedCompany: AdaptiveCompany
    ) {
        viewBinding.viewPager.adapter = viewPagerAdapter
        restoreSelectedTab(lastSelectedTabPosition)
        onDataChanged(
            companyName = selectedCompany.companyProfile.name,
            companySymbol = selectedCompany.companyProfile.symbol,
            favouriteIconResource = selectedCompany.companyStyle.favouriteForegroundIconResource
        )
    }

    fun onTabClicked(selectedTab: OrderedTextView) {
        if (isTabAlreadySelected(mSelectedTabPagePosition, selectedTab.orderNumber)) {
            return
        }

        val previousTab = getTabByPosition(mSelectedTabPagePosition)
        changeTabStyle(previousTab, selectedTab)
        viewBinding.viewPager.currentItem = selectedTab.orderNumber
        mSelectedTabPagePosition = selectedTab.orderNumber
    }

    fun onFavouriteIconClicked() {
        mViewAnimator.runScaleInOut(viewBinding.imageViewStar)
    }

    fun onDataChanged(companyName: String, companySymbol: String, favouriteIconResource: Int) {
        viewBinding.run {
            textViewCompanyName.text = companyName
            textViewCompanySymbol.text = companySymbol
            imageViewStar.setImageResource(favouriteIconResource)
        }
    }

    fun handleOnBackPressed(): Boolean {
        return if (isNotFirstPageSelected()) {
            viewBinding.viewPager.setCurrentItem(0, true)
            true
        } else false
    }

    private fun setUpViewPager() {
        viewBinding.viewPager.registerOnPageChangeCallback(mViewPagerCallback)
        viewBinding.viewPager.offscreenPageLimit = 5
    }

    private fun changeTabStyle(previousTab: OrderedTextView, newTab: OrderedTextView) {
        TextViewCompat.setTextAppearance(previousTab, R.style.textViewBodyShadowedTab)
        TextViewCompat.setTextAppearance(newTab, R.style.textViewH3Tab)
        mViewAnimator.runScaleInOut(newTab)
    }

    private fun restoreSelectedTab(lastSelectedTabPosition: Int) {
        if (!isCorrectTabSelected(lastSelectedTabPosition, mSelectedTabPagePosition)) {
            val newTab = getTabByPosition(lastSelectedTabPosition)
            val selectedTab = getTabByPosition(mSelectedTabPagePosition)
            changeTabStyle(selectedTab, newTab)
            mSelectedTabPagePosition = lastSelectedTabPosition
        }
    }

    private fun getTabByPosition(position: Int): OrderedTextView {
        return when (position) {
            0 -> viewBinding.textViewProfile
            1 -> viewBinding.textViewChart
            2 -> viewBinding.textViewNews
            3 -> viewBinding.textViewForecasts
            4 -> viewBinding.textViewIdeas
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