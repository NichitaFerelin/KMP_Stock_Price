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

package com.ferelin.stockprice.ui.login

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewAnimator
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.anim.AnimatorManager
import com.ferelin.stockprice.utils.invalidate

class LoginViewAnimator : BaseViewAnimator() {

    private lateinit var mSlideToBottomFadeIn: Animation
    private lateinit var mSlideToTopFadeOut: Animation
    private lateinit var mScaleIn: Animation
    private lateinit var mScaleOut: Animation
    private lateinit var mScaleInOut: Animator

    override fun loadAnimations(context: Context) {
        mSlideToBottomFadeIn =
            AnimationUtils.loadAnimation(context, R.anim.set_slide_to_end_fade_in)
        mSlideToTopFadeOut = AnimationUtils.loadAnimation(context, R.anim.set_slide_to_top_fade_out)
        mScaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
        mScaleInOut = AnimatorInflater.loadAnimator(context, R.animator.scale_in_out)
    }

    override fun invalidateAnimations() {
        mSlideToBottomFadeIn.invalidate()
        mSlideToTopFadeOut.invalidate()
        mScaleInOut.invalidate()
        mScaleIn.invalidate()
        mScaleOut.invalidate()
    }

    fun runSlideToBottomFadeIn(target: View) {
        target.startAnimation(mSlideToBottomFadeIn)
    }

    fun runSlideToTopFadeOut(target: View, listener: AnimationManager) {
        mSlideToTopFadeOut.setAnimationListener(listener)
        target.startAnimation(mSlideToTopFadeOut)
    }

    fun runScaleInOut(target: View, listener: AnimatorManager) {
        mScaleInOut.setTarget(target)
        mScaleInOut.addListener(listener)
        mScaleInOut.start()
    }

    fun runScaleIn(target: View, listener: AnimationManager? = null) {
        mScaleIn.setAnimationListener(listener)
        target.startAnimation(mScaleIn)
    }

    fun runScaleOut(target: View, listener: AnimationManager? = null) {
        mScaleOut.setAnimationListener(listener)
        target.startAnimation(mScaleOut)
    }
}