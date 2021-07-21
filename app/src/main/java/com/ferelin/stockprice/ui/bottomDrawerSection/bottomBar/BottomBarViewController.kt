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

package com.ferelin.stockprice.ui.bottomDrawerSection.bottomBar

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentBottomBarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomBarViewController :
    BaseViewController<BottomBarViewAnimator, FragmentBottomBarBinding>() {

    override val mViewAnimator = BottomBarViewAnimator()

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        if (savedInstanceState == null) {
            hideBottomBar()
        }
    }

    fun setArgumentsViewDependsOn(
        isBottomBarVisible: Boolean,
        isBottomBarFabVisible: Boolean,
        arrowState: Float
    ) {
        if (isBottomBarVisible) {
            showBottomBar()
        } else hideBottomBar()

        if (isBottomBarFabVisible) {
            viewBinding.mainFab.show()
        } else viewBinding.mainFab.hide()

        viewBinding.bottomAppBarImageViewArrowUp.rotation = arrowState
    }

    fun onBottomDrawerStateChanged(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN && viewBinding.bottomAppBar.isVisible) {
            viewBinding.mainFab.show()
        } else if (newState != BottomSheetBehavior.STATE_HIDDEN) {
            viewBinding.mainFab.hide()
        }
    }

    fun onFabClicked(isUserAuthenticated: Boolean) {
        if (!isUserAuthenticated) {
            mNavigator?.navigateToLoginFragment(false)
        }
    }

    fun onAuthenticationStateChanged(isUserAuthenticated: Boolean) {
        /**
         * Changes the icon depending on the authentication state.
         * */
        if (isUserAuthenticated) {
            viewBinding.mainFab.setImageResource(R.drawable.ic_user_photo)
            viewBinding.mainFab.contentDescription =
                context.getString(R.string.descriptionFabProfile)
        } else {
            viewBinding.mainFab.setImageResource(R.drawable.ic_key)
            viewBinding.mainFab.contentDescription =
                context.getString(R.string.descriptionFabLogIn)
        }
    }

    fun hideBottomBar() {
        with(viewBinding) {
            if (bottomAppBar.isVisible) {
                bottomAppBar.isVisible = false
            }

            mainFab.hide()
            bottomAppBar.performHide()
        }
    }

    fun showBottomBar() {
        with(viewBinding) {
            if (!bottomAppBar.isVisible) {
                bottomAppBar.isVisible = true
            }

            mainFab.show()
            bottomAppBar.performShow()
        }
    }
}