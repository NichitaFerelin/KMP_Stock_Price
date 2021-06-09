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
import androidx.appcompat.app.AlertDialog
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.common.menu.MenuItemType
import com.ferelin.stockprice.common.menu.MenuItemsAdapter
import com.ferelin.stockprice.databinding.FragmentMenuBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.utils.DataNotificator

class MenuViewController : BaseViewController<MenuViewAnimator, FragmentMenuBinding>() {

    override val mViewAnimator: MenuViewAnimator = MenuViewAnimator()

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
        onLogOut: () -> Unit
    ) {
        when (item.type) {
            is MenuItemType.LogIn -> Navigator.navigateToLoginFragment(currentFragment)
            is MenuItemType.LogOut -> showExitDialog(currentFragment.requireContext(), onLogOut)

            is MenuItemType.Messages -> { /*Messages */
            }
            is MenuItemType.Notes -> {/*Notes*/
            }
            is MenuItemType.Settings -> {/*Settings*/
            }
        }
    }

    fun setArgumentsViewDependsOn(menuItemsItemsAdapter: MenuItemsAdapter) {
        viewBinding.recyclerViewMenu.adapter = menuItemsItemsAdapter
    }

    fun onLogOut() {
        val recyclerViewAdapter = viewBinding.recyclerViewMenu.adapter
        if (recyclerViewAdapter is MenuItemsAdapter) {
            recyclerViewAdapter.onLogOutNotify()
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