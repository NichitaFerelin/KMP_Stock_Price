package com.ferelin.stockprice.common

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator

/**
 * [ViewAnimatorScrollable] providing default Fade, ScaleOut animations
 *  - @property: [mFadeOut]
 *  - @property: [mFadeIn]
 *  - @property: [mScaleOut]
 */
open class ViewAnimatorScrollable : BaseViewAnimator() {

    private var mFadeOut: Animation? = null
    private var mFadeIn: Animation? = null
    private var mScaleOut: Animation? = null

    override fun loadAnimations(context: Context) {
        mFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        mFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
    }

    override fun invalidateAnimations() {
        mFadeOut?.setAnimationListener(null)
        mFadeOut?.cancel()

        mFadeIn?.setAnimationListener(null)
        mFadeIn?.cancel()

        mScaleOut?.setAnimationListener(null)
        mScaleOut?.cancel()
    }

    fun runFadeInAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mFadeIn?.setAnimationListener(listener)
        target.startAnimation(mFadeIn)
    }

    fun runFadeOutAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mFadeOut?.setAnimationListener(listener)
        target.startAnimation(mFadeOut)
    }

    fun runScaleOutAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mScaleOut?.setAnimationListener(listener)
        target.startAnimation(mScaleOut)
    }
}