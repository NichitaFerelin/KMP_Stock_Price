package com.ferelin.stockprice.base

import android.content.Context

/**
 * [BaseViewAnimator] providing animations that can be triggered by [BaseViewController].
 */
abstract class BaseViewAnimator {
    abstract fun loadAnimations(context: Context)
    abstract fun invalidateAnimations()
}