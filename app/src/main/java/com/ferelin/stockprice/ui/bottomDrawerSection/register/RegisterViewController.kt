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

package com.ferelin.stockprice.ui.bottomDrawerSection.register

import android.animation.Animator
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentRegisterBinding
import com.ferelin.stockprice.utils.anim.AnimatorManager

class RegisterViewController : BaseViewController<RegisterViewAnimator, FragmentRegisterBinding>() {

    override val mViewAnimator = RegisterViewAnimator()

    fun onLoginChanged() {
        enableBtnCheck()
    }

    fun onError(message: String) {
        viewBinding.editTextLogin.error = message
        disableBtnCheck()
    }

    private fun enableBtnCheck() {
        viewBinding.imageViewIconCheck.isClickable = true
        switchBtnColor(R.color.green)
    }

    private fun disableBtnCheck() {
        viewBinding.imageViewIconCheck.isClickable = false
        switchBtnColor(R.color.grey)
    }

    private fun switchBtnColor(colorResource: Int) {
        with(viewBinding.imageViewIconCheck) {
            val animationCallback = object : AnimatorManager() {
                override fun onAnimationStart(animation: Animator?) {
                    setColorFilter(
                        ContextCompat.getColor(context, colorResource),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }
            mViewAnimator.runScaleInOut(this, animationCallback)
        }
    }
}