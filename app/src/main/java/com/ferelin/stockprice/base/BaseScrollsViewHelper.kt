package com.ferelin.stockprice.base

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R

/*
* BaseViewHelper with provided default anim for FAB.
* Can be applied to fragments where need to hide/show FAB.
* */
abstract class BaseScrollsViewHelper : BaseViewHelper() {

    private var mFadeOut: Animation? = null
    private var mFadeIn: Animation? = null
    private var mScaleOut: Animation? = null

    override fun prepare(context: Context) {
        mFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        mFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
    }

    override fun invalidate() {
        mFadeOut?.cancel()
        mFadeOut?.setAnimationListener(null)

        mFadeIn?.cancel()
        mFadeIn?.setAnimationListener(null)

        mScaleOut?.cancel()
        mScaleOut?.setAnimationListener(null)
    }

    fun runFadeIn(target: View, callback: Animation.AnimationListener? = null) {
        mFadeIn?.setAnimationListener(callback)
        target.startAnimation(mFadeIn)
    }

    fun runFadeOut(target: View, callback: Animation.AnimationListener? = null) {
        mFadeOut?.setAnimationListener(callback)
        target.startAnimation(mFadeOut)
    }

    fun runScaleOut(target: View, callback: Animation.AnimationListener? = null) {
        mScaleOut?.setAnimationListener(callback)
        target.startAnimation(mScaleOut)
    }
}