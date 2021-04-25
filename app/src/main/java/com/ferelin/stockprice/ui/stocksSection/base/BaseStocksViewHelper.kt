package com.ferelin.stockprice.ui.stocksSection.base

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseScrollsViewHelper

open class BaseStocksViewHelper : BaseScrollsViewHelper() {

    private var mScaleInLarge: Animation? = null
    private var mScaleOutLarge: Animation? = null

    override fun prepare(context: Context) {
        super.prepare(context)
        mScaleInLarge = AnimationUtils.loadAnimation(context, R.anim.scale_in_large)
        mScaleOutLarge = AnimationUtils.loadAnimation(context, R.anim.scale_out_large)
    }

    override fun invalidate() {
        super.invalidate()
        mScaleInLarge?.cancel()
        mScaleInLarge?.setAnimationListener(null)
        mScaleOutLarge?.cancel()
        mScaleOutLarge?.setAnimationListener(null)
    }

    fun runScaleInLarge(view: View, callback: Animation.AnimationListener? = null) {
        mScaleInLarge?.setAnimationListener(callback)
        view.startAnimation(mScaleInLarge)
    }

    fun runScaleOutLarge(view: View) {
        view.startAnimation(mScaleOutLarge)
    }
}