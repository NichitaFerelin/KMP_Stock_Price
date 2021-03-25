package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewHelper

class ChartViewHelper : BaseViewHelper() {

    private var mScaleInOut: Animator? = null

    override fun prepare(context: Context) {
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
    }

    override fun invalidate() {
        mScaleInOut?.apply {
            cancel()
            setTarget(null)
            removeAllListeners()
        }
    }

    fun runScaleInOut(target: View, callback: Animator.AnimatorListener? = null) {
        mScaleInOut?.let {
            it.setTarget(target)
            callback?.let { listener -> it.addListener(listener) }
            it.start()
        }
    }

    fun runAlphaIn(vararg targets: View) {
        targets.forEach { it.animate().alpha(1F).duration = 150L }
    }

    fun runAlphaOut(vararg targets: View) {
        targets.forEach { it.animate().alpha(0.0F).duration = 150L }
    }
}