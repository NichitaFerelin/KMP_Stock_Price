package com.ferelin.stockprice.ui.stocksSection.search

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewAnimator

class SearchViewAnimatorScrollable : BaseStocksViewAnimator() {

    private var mScaleOut: Animation? = null
    private var mScaleIn: Animation? = null

    override fun loadAnimations(context: Context) {
        super.loadAnimations(context)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
        mScaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
    }

    override fun invalidateAnimations() {
        super.invalidateAnimations()
        mScaleOut?.cancel()
        mScaleIn?.cancel()
    }

    fun runScaleIn(target: View, callback: Animation.AnimationListener? = null) {
        mScaleIn?.setAnimationListener(callback)
        target.startAnimation(mScaleIn)
    }
}