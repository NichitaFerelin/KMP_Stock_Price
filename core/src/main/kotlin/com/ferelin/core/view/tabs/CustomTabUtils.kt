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

package com.ferelin.core.view.tabs

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat

internal object CustomTabUtils {

    fun getStart(v: View?): Int {
        return when {
            v == null -> 0
            isLayoutRtl(v) -> v.right - getPaddingStart(v)
            else -> v.left + getPaddingStart(v)
        }
    }

    fun getEnd(v: View?): Int {
        return when {
            v == null -> 0
            isLayoutRtl(v) -> v.left + getPaddingEnd(v)
            else -> v.right - getPaddingEnd(v)
        }
    }

    fun getMarginStart(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginStart(lp)
    }

    fun getMarginEnd(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginEnd(lp)
    }

    fun getMarginHorizontally(v: View?): Int {
        if (v == null) {
            return 0
        }
        val lp = v.layoutParams as MarginLayoutParams
        return MarginLayoutParamsCompat.getMarginStart(lp) + MarginLayoutParamsCompat.getMarginEnd(
            lp
        )
    }

    fun isLayoutRtl(v: View?): Boolean {
        return ViewCompat.getLayoutDirection(v!!) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    private fun getPaddingStart(v: View?): Int {
        return if (v == null) {
            0
        } else ViewCompat.getPaddingStart(v)
    }

    private fun getPaddingEnd(v: View?): Int {
        return if (v == null) {
            0
        } else ViewCompat.getPaddingEnd(v)
    }
}