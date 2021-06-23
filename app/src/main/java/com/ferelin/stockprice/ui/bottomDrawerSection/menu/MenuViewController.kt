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

package com.ferelin.stockprice.ui.bottomDrawerSection.menu

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentMenuBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.bottomDrawerSection.login.LoginFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItemType
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItemsAdapter
import com.ferelin.stockprice.ui.bottomDrawerSection.register.RegisterFragment
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.getString

class MenuViewController : BaseViewController<MenuViewAnimator, FragmentMenuBinding>() {

    override val mViewAnimator: MenuViewAnimator = MenuViewAnimator()

    private var mIsWaitingForLoginResult = false
    private var mIsWaitingForRegisterResult = false

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding.recyclerViewMenu.adapter = null
            super.onDestroyView()
        }
    }

    fun onMenuItemsPrepared(notificator: DataNotificator<List<MenuItem>>) {
        val recyclerViewAdapter = viewBinding.recyclerViewMenu.adapter
        if (recyclerViewAdapter is MenuItemsAdapter) {
            recyclerViewAdapter.setData(notificator.data!!)
        }
    }

    fun onMenuItemClicked(
        currentFragment: MenuFragment,
        item: MenuItem,
        isUserAuthenticated: Boolean,
        isUserRegistered: Boolean,
        onLogOut: () -> Unit
    ) {
        when (item.type) {
            is MenuItemType.LogIn -> Navigator.navigateToLoginFragment(currentFragment)
            is MenuItemType.LogOut -> showExitDialog(currentFragment.requireContext(), onLogOut)

            is MenuItemType.Messages -> navigateToRelations(
                currentFragment,
                isUserAuthenticated,
                isUserRegistered
            )
            is MenuItemType.Notes -> {/*Notes*/
            }
            is MenuItemType.Settings -> {/*Settings*/
            }
        }
    }

    fun setArgumentsViewDependsOn(
        menuItemsAdapter: MenuItemsAdapter,
        isWaitingForLoginResult: Boolean,
        isWaitingForRegisterResult: Boolean,
        isUserAuthenticated: Boolean
    ) {
        viewBinding.recyclerViewMenu.adapter = menuItemsAdapter
        mIsWaitingForLoginResult = isWaitingForLoginResult
        mIsWaitingForRegisterResult = isWaitingForRegisterResult

        val titleText = if (isUserAuthenticated) {
            getString(context, R.string.titleAuthorized)
        } else getString(context, R.string.titleNotAuthorized)
        viewBinding.textViewAuthorization.text = titleText
    }

    fun onLoginResult(currentFragment: MenuFragment, arguments: Bundle, isUserRegistered: Boolean) {
        if (!mIsWaitingForLoginResult) {
            return
        }

        val isUserAuthenticated = arguments[LoginFragment.LOGIN_LOG_STATE_KEY]
        if (isUserAuthenticated is Boolean && isUserAuthenticated == true) {
            navigateToRelations(currentFragment, isUserAuthenticated, isUserRegistered)
        }
    }

    fun onRegisterResult(currentFragment: MenuFragment, arguments: Bundle) {
        if (!mIsWaitingForRegisterResult) {
            return
        }

        val isUserRegistered = arguments[RegisterFragment.REGISTER_REQUEST_KEY]
        if (isUserRegistered is Boolean && isUserRegistered == true) {
            navigateToRelations(
                currentFragment,
                isUserAuthenticated = true,
                isUserRegistered = true
            )
        }
    }

    fun onLogOut() {
        try {
            viewBinding.textViewAuthorization.text = getString(context, R.string.titleNotAuthorized)
            val recyclerViewAdapter = viewBinding.recyclerViewMenu.adapter
            if (recyclerViewAdapter is MenuItemsAdapter) {
                recyclerViewAdapter.onLogOutNotify()
            }
        } catch (e: IllegalStateException) {
            /*
            * viewBinding can be null at this moment.
            *
            * When user returns to the fragment that contains this menu, the view of this menu-fragment
            * quickly goes through a full lifecycle from onCreateView to onDestroyView and
            * then is created on a new one from onCreateView.
            *
            * I don't understand why this is happening
            * */
        }
    }

    private fun navigateToRelations(
        currentFragment: MenuFragment,
        isUserAuthenticated: Boolean,
        isUserRegistered: Boolean
    ) {
        when {
            isUserAuthenticated && isUserRegistered -> {
                Navigator.navigateToRelationsFragment(currentFragment)
            }
            !isUserAuthenticated -> {
                currentFragment.onWaitingForLoginResult()
                Navigator.navigateToLoginFragment(currentFragment)
            }
            !isUserRegistered -> {
                currentFragment.onWaitingForRegisterResult()
                Navigator.navigateToRegisterFragment(currentFragment)
            }
        }
    }

    private fun showExitDialog(context: Context, onLogOut: () -> Unit) {
        AlertDialog.Builder(context)
            .setMessage(R.string.hintAreYouSure)
            .setCancelable(true)
            .setPositiveButton(R.string.hintYes) { _, _ -> onLogOut.invoke() }
            .setNegativeButton(R.string.hintNo) { dialog, _ -> dialog.cancel() }
            .show()
    }
}