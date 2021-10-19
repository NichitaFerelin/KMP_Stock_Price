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

package com.ferelin.core.adapter.base

import android.view.animation.Animation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.withTimer

fun RecyclerView.LayoutManager?.ifLinear(action: (LinearLayoutManager) -> Unit) {
    if (this is LinearLayoutManager) {
        action.invoke(this)
    }
}

fun RecyclerView.scrollToTopWithCustomAnim(
    fadeIn: Animation,
    fadeOut: Animation,
    customItemAnimator: RecyclerView.ItemAnimator? = null
) {
    // starts smooth scrolling to top -> fades out -> hard scroll to 20 position ->
    // fade in -> smooth scroll to 0

    val fadeInCallback = object : AnimationManager() {
        override fun onAnimationStart(animation: Animation?) {
            alpha = 1F
            smoothScrollToPosition(0)
        }

        override fun onAnimationEnd(animation: Animation?) {
            // To avoid graphic bug
            withTimer { itemAnimator = customItemAnimator }
        }
    }

    fadeIn.setAnimationListener(fadeInCallback)

    val fadeOutCallback = object : AnimationManager() {
        override fun onAnimationStart(animation: Animation?) {
            // To avoid graphic bug
            itemAnimator = null

            smoothScrollBy(0, -height)
        }

        override fun onAnimationEnd(animation: Animation?) {
            alpha = 0F
            scrollToPosition(20)
            startAnimation(fadeIn)
        }
    }

    fadeOut.setAnimationListener(fadeOutCallback)
    startAnimation(fadeOut)
}