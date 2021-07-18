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
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentLoginBinding
import com.ferelin.stockprice.utils.*
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.anim.AnimatorManager

class LoginViewController : BaseViewController<LoginViewAnimator, FragmentLoginBinding>() {

    override val mViewAnimator: LoginViewAnimator = LoginViewAnimator()

    private var mLastInputCode = ""
    private var mLastInputPhone = ""
    private var mCheckIconVisibility = View.VISIBLE
    private var mCheckIconScale = 1F
    private var mEnterCodeAlpha = 0F

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }

        mLastInputCode = viewBinding.editTextCode.text.toString()
        mLastInputPhone = viewBinding.editTextPhone.text.toString()
    }

    override fun onDestroyView() {
        with(viewBinding) {
            mCheckIconVisibility = imageViewIconCheck.visibility
            mEnterCodeAlpha = editTextCodeLayout.alpha
        }
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(sCheckIconScaleKey, mCheckIconScale)
        outState.putFloat(sEnterCodeAlphaKey, mEnterCodeAlpha)
    }

    fun onBackPressed() {
        mNavigator?.navigateBackToHostFragment()
    }

    fun onPhoneNumberChanged(phone: String) {
        with(viewBinding) {
            if (phone == mLastInputPhone) {
                return
            }

            mLastInputPhone = phone
            viewBinding.editTextCode.setText("")

            when {
                phone.isNotEmpty() && !isBtnCheckEnabled() -> enableBtnCheck()
                phone.isEmpty() && isBtnCheckEnabled() -> disableBtnCheck()
            }

            if (imageViewIconCheck.isOut) {
                showBtnCheck()
            }

            if (!progressBar.isOut) {
                hideProgressBar()
            }

            if (!editTextCodeLayout.isOut) {
                hideEditTextCode()
            }
        }
    }

    fun onSendCodeClicked() {
        invalidateErrors()
        hideBtnCheck()
    }

    fun onCodeSentStateChanged(isCodeSent: Boolean) {
        if (isCodeSent) {
            hideBtnCheck()
            showEditTextCode()
            setFocus(viewBinding.editTextCode)
            invalidateErrors()
        } else {
            hideEditTextCode()
            showBtnCheck()
        }
    }

    fun onCodeChanged() {
        with(viewBinding) {
            val code = editTextCode.text.toString()
            if (code == mLastInputCode) {
                return
            }

            mLastInputCode = code
            if (code.length == 6) {
                hideKeyboard(root.context, root)
            }
        }
    }

    fun onLoadingStateChanged(isLoading: Boolean) {
        when {
            isLoading -> {
                invalidateErrors()
                showProgressBar()
                hideBtnCheck()
            }
            !isLoading -> hideProgressBar()
        }
    }

    fun onError(message: String) {
        when {
            viewBinding.editTextCode.isFocused -> viewBinding.editTextCode.error = message
            viewBinding.editTextPhone.isFocused -> viewBinding.editTextPhone.error = message
        }
    }

    fun onSignIn() {
        showDefaultDialog(context, getString(context, R.string.hintAuthorization))
        mNavigator?.navigateBackToHostFragment()
    }

    private fun invalidateErrors() {
        viewBinding.editTextCode.error = null
        viewBinding.editTextPhone.error = null
    }

    private fun showProgressBar() {
        viewBinding.progressBar.apply {
            scaleY = 1F
            scaleX = 1F
            mViewAnimator.runScaleIn(this)
        }
    }

    private fun hideProgressBar() {
        viewBinding.progressBar.apply {
            scaleY = 0F
            scaleX = 0F
            mViewAnimator.runScaleOut(this)
        }
    }

    private fun setFocus(target: View) {
        target.requestFocus()
        openKeyboard(context, target)
    }

    private fun enableBtnCheck() {
        viewBinding.imageViewIconCheck.isClickable = true
        switchBtnColor(R.color.green)
    }

    private fun disableBtnCheck() {
        viewBinding.imageViewIconCheck.isClickable = false
        switchBtnColor(R.color.grey)
    }

    private fun isBtnCheckEnabled(): Boolean {
        return viewBinding.imageViewIconCheck.isClickable
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

    private fun hideBtnCheck() {
        with(viewBinding.imageViewIconCheck) {
            val animationCallback = object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    scaleX = 0F
                    scaleY = 0F
                }
            }
            mViewAnimator.runScaleOut(this, animationCallback)
        }
    }

    private fun showBtnCheck() {
        with(viewBinding.imageViewIconCheck) {
            scaleX = 1F
            scaleY = 1F
            mViewAnimator.runScaleIn(this)
        }
    }

    private fun showEditTextCode() {
        viewBinding.editTextCodeLayout.alpha = 1F
        mViewAnimator.runSlideToBottomFadeIn(viewBinding.editTextCodeLayout)
    }

    private fun hideEditTextCode() {
        val callback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding.editTextCodeLayout.alpha = 0F
            }
        }
        mViewAnimator.runSlideToTopFadeOut(viewBinding.editTextCodeLayout, callback)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        val checkIconScale = savedInstanceState.getFloat(sCheckIconScaleKey)
        val enterCodeAlpha = savedInstanceState.getFloat(sEnterCodeAlphaKey)
        viewBinding.run {
            editTextCodeLayout.alpha = enterCodeAlpha
            imageViewIconCheck.scaleX = checkIconScale
            imageViewIconCheck.scaleY = checkIconScale
        }
    }

    companion object {
        private const val sCheckIconScaleKey = "check_icon_scale_key"
        private const val sEnterCodeAlphaKey = "enter_code_key"
    }
}