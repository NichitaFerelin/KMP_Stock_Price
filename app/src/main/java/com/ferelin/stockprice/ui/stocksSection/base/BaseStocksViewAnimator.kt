package com.ferelin.stockprice.ui.stocksSection.base

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.common.ViewAnimatorScrollable

open class BaseStocksViewAnimator : ViewAnimatorScrollable() {

    private var mScaleInLarge: Animation? = null
    private var mScaleOutLarge: Animation? = null

    override fun loadAnimations(context: Context) {
        super.loadAnimations(context)
        mScaleInLarge = AnimationUtils.loadAnimation(context, R.anim.scale_in_large)
        mScaleOutLarge = AnimationUtils.loadAnimation(context, R.anim.scale_out_large)
    }

    override fun invalidateAnimations() {
        super.invalidateAnimations()
        mScaleInLarge?.cancel()
        mScaleOutLarge?.cancel()
    }

    fun runScaleInLarge(view: View, callback: Animation.AnimationListener? = null) {
        mScaleInLarge?.setAnimationListener(callback)
        view.startAnimation(mScaleInLarge)
    }

    fun runScaleOutLarge(view: View) {
        view.startAnimation(mScaleOutLarge)
    }
}