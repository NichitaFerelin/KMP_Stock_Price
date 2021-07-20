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

package com.ferelin.stockprice.ui.bottomDrawerSection

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentBottomDrawerBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions.AlphaSlideAction
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItemType
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItemsAdapter
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.bottomDrawer.BottomSheetManager
import com.ferelin.stockprice.utils.bottomDrawer.OnSlideAction
import com.ferelin.stockprice.utils.bottomDrawer.OnStateAction
import com.ferelin.stockprice.utils.isHidden
import com.ferelin.stockprice.utils.themeColor
import com.ferelin.stockprice.utils.withTimerOnUi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable

class BottomDrawerViewController :
    BaseViewController<BottomDrawerViewAnimator, FragmentBottomDrawerBinding>() {

    override val mViewAnimator: BottomDrawerViewAnimator = BottomDrawerViewAnimator()

    private var mBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    private val mBottomSheetManager: BottomSheetManager by lazy(LazyThreadSafetyMode.NONE) {
        BottomSheetManager()
    }

    val isDrawerHidden: Boolean
        get() = mBottomSheetBehavior?.isHidden() ?: true

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        initBottomDrawer()
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding.recyclerViewMenu.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(
        menuItemsAdapter: MenuItemsAdapter,
        scrimVisibility: Int
    ) {
        viewBinding.recyclerViewMenu.adapter = menuItemsAdapter
        restoreScrimState(scrimVisibility)
    }

    fun onMenuItemsPrepared(notificator: DataNotificator<List<MenuItem>>) {
        val recyclerViewAdapter = viewBinding.recyclerViewMenu.adapter
        if (recyclerViewAdapter is MenuItemsAdapter) {
            recyclerViewAdapter.setData(notificator.data!!)
        }
    }

    fun onMenuItemClicked(item: MenuItem, onLogOut: () -> Unit) {
        if (item.type != MenuItemType.LogOut) {
            withTimerOnUi(200) { closeDrawer() }
        }

        when (item.type) {
            MenuItemType.Stocks -> mNavigator?.navigateToStocksPagerFragment()
            MenuItemType.Chats -> mNavigator?.navigateToChatsFragment()
            MenuItemType.LogIn -> mNavigator?.navigateToLoginFragment(true)
            MenuItemType.LogOut -> showExitDialog(context, onLogOut)
            else -> Unit
        }
    }

    fun addOnSlideAction(action: OnSlideAction) {
        mBottomSheetManager.addOnSlideAction(action)
    }

    fun addOnStateAction(action: OnStateAction) {
        mBottomSheetManager.addOnStateAction(action)
    }

    fun handleOnBackPressed(): Boolean {
        return if (mBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED
            || mBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HALF_EXPANDED
        ) {
            closeDrawer()
            true
        } else false
    }

    fun openDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    fun closeDrawer() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initBottomDrawer() {
        with(viewBinding) {
            mBottomSheetBehavior = BottomSheetBehavior.from(containerRoot).also {
                it.addBottomSheetCallback(mBottomSheetManager)
                it.state = BottomSheetBehavior.STATE_HIDDEN
            }
            addOnSlideAction(AlphaSlideAction(viewScrim))
            containerRoot.background = MaterialShapeDrawable(
                context,
                null,
                R.attr.bottomSheetStyle,
                0
            ).apply {
                fillColor = ColorStateList.valueOf(context.themeColor(R.attr.colorPrimary))
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

    private fun restoreScrimState(scrimVisibility: Int) {
        if (scrimVisibility != View.GONE) {
            viewBinding.viewScrim.visibility = scrimVisibility
            viewBinding.viewScrim.alpha = 1F
        }
    }
}