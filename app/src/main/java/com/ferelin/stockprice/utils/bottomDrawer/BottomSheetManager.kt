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

package com.ferelin.stockprice.utils.bottomDrawer

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions.OnStateChangedAction
import com.ferelin.stockprice.utils.normalize
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.max

class BottomSheetManager : BottomSheetBehavior.BottomSheetCallback() {

    private val mOnSlideActions: MutableList<OnSlideAction> = mutableListOf()
    private val mOnStateActions: MutableList<OnStateChangedAction> = mutableListOf()

    private var mLastSlideOffset = -1.0F
    private var mHalfExpandedSlideOffset = Float.MAX_VALUE

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        mOnStateActions.forEach { it.onStateChanged(newState) }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (mHalfExpandedSlideOffset == Float.MAX_VALUE)
            calculateInitialHalfExpandedSlideOffset(bottomSheet)

        mLastSlideOffset = slideOffset

        // Correct for the fact that the slideOffset is not zero when half expanded
        val trueOffset = if (slideOffset <= mHalfExpandedSlideOffset) {
            slideOffset.normalize(-1F, mHalfExpandedSlideOffset, -1F, 0F)
        } else {
            slideOffset.normalize(mHalfExpandedSlideOffset, 1F, 0F, 1F)
        }

        mOnSlideActions.forEach { it.onSlide(bottomSheet, trueOffset) }
    }

    fun addOnSlideAction(action: OnSlideAction): Boolean {
        return mOnSlideActions.add(action)
    }

    fun addOnStateAction(action: OnStateChangedAction): Boolean {
        return mOnStateActions.add(action)
    }

    private fun calculateInitialHalfExpandedSlideOffset(sheet: View) {
        val parent = sheet.parent as CoordinatorLayout
        val behavior = BottomSheetBehavior.from(sheet)

        val halfExpandedOffset = parent.height * (1 - behavior.halfExpandedRatio)
        val peekHeightMin = parent.resources.getDimensionPixelSize(
            R.dimen.design_bottom_sheet_peek_height_min
        )
        val peek = max(peekHeightMin, parent.height - parent.width * 9 / 16)
        val collapsedOffset = max(
            parent.height - peek,
            max(0, parent.height - sheet.height)
        )
        mHalfExpandedSlideOffset =
            (collapsedOffset - halfExpandedOffset) / (parent.height - collapsedOffset)
    }
}