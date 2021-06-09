package com.ferelin.stockprice.common

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator
import com.ferelin.stockprice.utils.invalidate

/**
 * [ViewAnimatorScrollable] provides default Fade IN/OUT, ScaleOut animations
 */
open class ViewAnimatorScrollable : BaseViewAnimator() {

    private lateinit var mFadeOut: Animation
    private lateinit var mFadeIn: Animation
    private lateinit var mScaleOut: Animation

    override fun loadAnimations(context: Context) {
        mFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        mFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
    }

    override fun invalidateAnimations() {
        mFadeOut.invalidate()
        mFadeIn.invalidate()
        mScaleOut.invalidate()
    }

    fun runFadeInAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mFadeIn.setAnimationListener(listener)
        target.startAnimation(mFadeIn)
    }

    fun runFadeOutAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mFadeOut.setAnimationListener(listener)
        target.startAnimation(mFadeOut)
    }

    fun runScaleOutAnimation(target: View, listener: Animation.AnimationListener? = null) {
        mScaleOut.setAnimationListener(listener)
        target.startAnimation(mScaleOut)
    }
}