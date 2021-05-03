package com.ferelin.stockprice.ui.stocksSection.stocksPager

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator

class StocksPagerViewAnimator : BaseViewAnimator() {

    private lateinit var mScaleInOut: Animator
    private lateinit var mScaleOut: Animation

    override fun loadAnimations(context: Context) {
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
    }

    override fun invalidateAnimations() {
        mScaleInOut.setTarget(null)
        mScaleInOut.removeAllListeners()
        mScaleOut.setAnimationListener(null)
    }

    fun runScaleInOut(target: View) {
        mScaleInOut.setTarget(target)
        mScaleInOut.start()
    }

    fun runScaleOut(target: View, callback: Animation.AnimationListener? = null) {
        mScaleOut.setAnimationListener(callback)
        target.startAnimation(mScaleOut)
    }
}