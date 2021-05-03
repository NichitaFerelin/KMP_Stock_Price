package com.ferelin.stockprice.ui.aboutSection.aboutSection

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator

class AboutPagerViewAnimator : BaseViewAnimator() {

    private var mScaleInOut: Animator? = null

    override fun loadAnimations(context: Context) {
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
    }

    override fun invalidateAnimations() {
        mScaleInOut?.cancel()
    }

    fun runScaleInOut(target: View) {
        mScaleInOut?.setTarget(target)
        mScaleInOut?.start()
    }
}