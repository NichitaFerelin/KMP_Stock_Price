package com.ferelin.stockprice.ui.aboutSection.chart

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