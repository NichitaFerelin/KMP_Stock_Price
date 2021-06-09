package com.ferelin.stockprice.ui.stocksSection.search

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
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewAnimator
import com.ferelin.stockprice.utils.invalidate

class SearchViewAnimator : BaseStocksViewAnimator() {

    private lateinit var mScaleOut: Animation
    private lateinit var mScaleIn: Animation

    override fun loadAnimations(context: Context) {
        super.loadAnimations(context)
        mScaleOut = AnimationUtils.loadAnimation(context, R.anim.scale_out)
        mScaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
    }

    override fun invalidateAnimations() {
        super.invalidateAnimations()
        mScaleOut.invalidate()
        mScaleIn.invalidate()
    }

    fun runScaleIn(target: View, callback: Animation.AnimationListener? = null) {
        mScaleIn.setAnimationListener(callback)
        target.startAnimation(mScaleIn)
    }
}