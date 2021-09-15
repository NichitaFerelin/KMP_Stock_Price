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

package com.ferelin.stockprice.ui.previewSection.loading

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentLoadingBinding
import com.ferelin.stockprice.utils.anim.MotionManager

class LoadingViewController : BaseViewController<LoadingViewAnimator, FragmentLoadingBinding>() {

    override val mViewAnimator: LoadingViewAnimator = LoadingViewAnimator()

    fun onFirstTimeStateChanged(isFirstTimeLaunch: Boolean?) {
        isFirstTimeLaunch?.let { initFragmentReplace(it) }
    }

    private fun initFragmentReplace(isFirstTimeLaunch: Boolean) {
        viewBinding.root.setTransitionListener(object : MotionManager() {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                super.onTransitionCompleted(p0, p1)
                removeAutoTransition()
                replaceFragment(isFirstTimeLaunch)
            }
        })
    }

    // Stops transition cycle
    private fun removeAutoTransition() {
        viewBinding.root.getTransition(R.id.transitionMain).autoTransition =
            MotionScene.Transition.AUTO_NONE
        viewBinding.root.getTransition(R.id.transitionJump).autoTransition =
            MotionScene.Transition.AUTO_NONE
    }

    private fun replaceFragment(isFirstTimeLaunch: Boolean) {
        if (isFirstTimeLaunch) {
            mNavigator?.navigateToWelcomeFragment()
        } else mNavigator?.navigateToDrawerHostFragment()
    }
}