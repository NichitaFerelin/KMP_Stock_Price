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

package com.ferelin.core.customView.tabs

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.core.R
import kotlin.math.roundToInt

class CustomTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    private val customTabTrip = CustomTabTrip(context, attrs)

    init {
        isHorizontalScrollBarEnabled = false
        isFillViewport = false

        addView(customTabTrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private var attachedViewPager: ViewPager2? = null
    private val viewPagerListener by lazy(LazyThreadSafetyMode.NONE) {
        InternalViewPagerListener()
    }

    private val internalTabClickListener = InternalTabClickListener()

    private val tabViewTextHorizontalPadding =
        (TAB_PADDING * resources.displayMetrics.density).toInt()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (customTabTrip.childCount > 0) {
            val firstTab = customTabTrip.getChildAt(0)
            val lastTab = customTabTrip.getChildAt(customTabTrip.childCount - 1)

            val start =
                (w - firstTab.measuredWidth) / 2 - CustomTabUtils.getMarginStart(firstTab) + 44
            val end = (w - lastTab.measuredWidth) / 2 - CustomTabUtils.getMarginEnd(lastTab)

            customTabTrip.minimumWidth = customTabTrip.measuredWidth

            ViewCompat.setPaddingRelative(this, start, paddingTop, end, paddingBottom)

            clipToPadding = false
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (changed && attachedViewPager != null) {
            scrollToTab(attachedViewPager!!.currentItem, 0f)
        }
    }

    fun attachViewPager(viewPager: ViewPager2, vararg titles: String) {
        customTabTrip.removeAllViews()

        attachedViewPager = viewPager
        attachedViewPager!!.registerOnPageChangeCallback(viewPagerListener)

        populateTabStrip(*titles)
    }

    fun detachViewPager() {
        attachedViewPager?.unregisterOnPageChangeCallback(viewPagerListener)
        attachedViewPager = null
    }

    private fun populateTabStrip(vararg titles: String) {
        titles.forEachIndexed { index, title ->
            val tabView = createDefaultTabView(title)
            tabView.setOnClickListener(internalTabClickListener)
            customTabTrip.addView(tabView)

            if (index == attachedViewPager!!.currentItem) {
                tabView.isSelected = true
            }
        }
    }

    private fun createDefaultTabView(title: CharSequence?): TextView {
        return TextView(context).apply {
            text = title
            gravity = Gravity.CENTER
            setPadding(
                tabViewTextHorizontalPadding, 0,
                tabViewTextHorizontalPadding, 0
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(R.style.textViewH3)
            }
            setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Float) {
        if (customTabTrip.childCount == 0 || tabIndex < 0 || tabIndex >= customTabTrip.childCount) {
            return
        }

        val isLayoutRtl = CustomTabUtils.isLayoutRtl(this)
        val selectedTab = customTabTrip.getChildAt(tabIndex)

        val widthPlusMargin = selectedTab.width + CustomTabUtils.getMarginHorizontally(selectedTab)
        var extraOffset = (positionOffset * widthPlusMargin).toInt()

        if (0f < positionOffset && positionOffset < 1f) {
            val nextTab = customTabTrip.getChildAt(tabIndex + 1)

            val selectHalfWidth = selectedTab.width / 2 + CustomTabUtils.getMarginEnd(selectedTab)
            val nextHalfWidth = nextTab.width / 2 + CustomTabUtils.getMarginStart(nextTab)

            extraOffset = (positionOffset * (selectHalfWidth + nextHalfWidth)).roundToInt()
        }

        val firstTab = customTabTrip.getChildAt(0)
        var x: Int

        if (isLayoutRtl) {
            val first = firstTab.width + CustomTabUtils.getMarginEnd(firstTab)
            val selected = selectedTab.width + CustomTabUtils.getMarginEnd(selectedTab)
            x =
                CustomTabUtils.getEnd(selectedTab) - CustomTabUtils.getMarginEnd(selectedTab) - extraOffset
            x -= (first - selected) / 2
        } else {
            val first = firstTab.width + CustomTabUtils.getMarginStart(firstTab)
            val selected = selectedTab.width + CustomTabUtils.getMarginStart(selectedTab)
            x =
                CustomTabUtils.getStart(selectedTab) - CustomTabUtils.getMarginStart(selectedTab) + extraOffset
            x -= (first - selected) / 2
        }

        scrollTo(x, 0)
        return
    }

    private inner class InternalViewPagerListener : ViewPager2.OnPageChangeCallback() {

        private var mScrollState = 0

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (
                customTabTrip.childCount == 0
                || position < 0
                || position >= customTabTrip.childCount
            ) return

            customTabTrip.onViewPagerPageChanged(position, positionOffset)
            scrollToTab(position, positionOffset)
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                customTabTrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0f)
            }

            var index = 0
            val size = customTabTrip.childCount
            while (index < size) {
                customTabTrip.getChildAt(index).isSelected = position == index
                index++
            }
        }
    }

    private inner class InternalTabClickListener : OnClickListener {

        override fun onClick(v: View) {
            for (index in 0 until customTabTrip.childCount) {
                if (v === customTabTrip.getChildAt(index)) {
                    attachedViewPager!!.currentItem = index
                    return
                }
            }
        }
    }

    private companion object {
        private const val TAB_PADDING = 16
    }
}
