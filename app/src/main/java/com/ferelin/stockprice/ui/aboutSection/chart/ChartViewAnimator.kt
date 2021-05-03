package com.ferelin.stockprice.ui.aboutSection.chart

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator

class ChartViewAnimator : BaseViewAnimator() {

    private var mScaleInOut: Animator? = null
    private val mViewsPropertyAnimator = hashMapOf<View, Any?>()

    override fun loadAnimations(context: Context) {
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
    }

    override fun invalidateAnimations() {
        mScaleInOut?.cancel()
        mViewsPropertyAnimator.keys.forEach { it.animate().cancel() }
    }

    fun runScaleInOut(target: View, callback: Animator.AnimatorListener? = null) {
        mScaleInOut?.let {
            it.setTarget(target)
            callback?.let { listener -> it.addListener(listener) }
            it.start()
        }
    }

    fun runFadeIn(vararg targets: View) {
        targets.forEach {
            mViewsPropertyAnimator[it] = null
            it.animate().alpha(1F).duration = 150L
        }
    }

    fun runFadeOut(vararg targets: View) {
        targets.forEach { it.animate().alpha(0F).duration = 150L }
    }
}