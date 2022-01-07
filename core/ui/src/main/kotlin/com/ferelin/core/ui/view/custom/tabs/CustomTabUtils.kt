package com.ferelin.core.ui.view.custom.tabs

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.ViewCompat

internal object CustomTabUtils {
  fun getStart(view: View?): Int {
    return when {
      view == null -> 0
      isLayoutRtl(view) -> view.right - getPaddingStart(view)
      else -> view.left + getPaddingStart(view)
    }
  }

  fun getEnd(view: View?): Int {
    return when {
      view == null -> 0
      isLayoutRtl(view) -> view.left + getPaddingEnd(view)
      else -> view.right - getPaddingEnd(view)
    }
  }

  fun getMarginStart(view: View?): Int {
    if (view == null) {
      return 0
    }

    val lp = view.layoutParams as MarginLayoutParams
    return MarginLayoutParamsCompat.getMarginStart(lp)
  }

  fun getMarginEnd(view: View?): Int {
    if (view == null) {
      return 0
    }

    val lp = view.layoutParams as MarginLayoutParams
    return MarginLayoutParamsCompat.getMarginEnd(lp)
  }

  fun getMarginHorizontally(view: View?): Int {
    if (view == null) {
      return 0
    }

    val lp = view.layoutParams as MarginLayoutParams
    return MarginLayoutParamsCompat.getMarginStart(lp) +
      MarginLayoutParamsCompat.getMarginEnd(lp)
  }

  fun isLayoutRtl(v: View?): Boolean {
    return ViewCompat.getLayoutDirection(v!!) == ViewCompat.LAYOUT_DIRECTION_RTL
  }

  private fun getPaddingStart(v: View?): Int {
    return if (v == null) {
      0
    } else {
      ViewCompat.getPaddingStart(v)
    }
  }

  private fun getPaddingEnd(v: View?): Int {
    return if (v == null) {
      0
    } else {
      ViewCompat.getPaddingEnd(v)
    }
  }
}