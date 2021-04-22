package com.ferelin.stockprice.ui.aboutSection.aboutSection

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
        mScaleInOut?.cancel()
        mScaleInOut?.setTarget(null)
        mScaleInOut?.removeAllListeners()
    }

    fun runScaleInOut(target: View) {
        mScaleInOut?.setTarget(target)
        mScaleInOut?.start()
    }
}