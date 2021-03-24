package com.ferelin.stockprice.ui.stocksSection.search

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewHelper

class SearchViewHelper : BaseViewHelper() {

    private lateinit var mScaleOut: Animation
    private lateinit var mScaleIn: Animation

    override fun prepare(context: Context) {
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
        mScaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
    }

    override fun invalidate() {
        mScaleOut.setAnimationListener(null)
        mScaleIn.setAnimationListener(null)
    }

    fun runScaleOut(target: View, callback: Animation.AnimationListener? = null) {
        mScaleOut.setAnimationListener(callback)
        target.startAnimation(mScaleOut)
    }

    fun runScaleIn(target: View, callback: Animation.AnimationListener? = null) {
        mScaleIn.setAnimationListener(callback)
        target.startAnimation(mScaleIn)
    }
}