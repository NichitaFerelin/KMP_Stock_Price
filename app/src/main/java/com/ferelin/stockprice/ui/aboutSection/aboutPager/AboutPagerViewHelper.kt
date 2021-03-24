package com.ferelin.stockprice.ui.aboutSection.aboutPager

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewHelper

class AboutPagerViewHelper : BaseViewHelper() {

    private var mScaleInOut: Animator? = null

    override fun prepare(context: Context) {
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
    }

    override fun invalidate() {
        mScaleInOut?.let {
            it.setTarget(null)
            it.removeAllListeners()
        }
    }

    fun runScaleInOut(target: View) {
        mScaleInOut?.let {
            it.setTarget(target)
            it.start()
        }
    }
}