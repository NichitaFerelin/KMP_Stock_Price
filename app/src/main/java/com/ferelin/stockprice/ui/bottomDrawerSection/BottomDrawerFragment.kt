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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentBottomDrawerBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItemClickListener
import com.ferelin.stockprice.utils.bottomDrawer.OnSlideAction
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomDrawerFragment :
    BaseFragment<FragmentBottomDrawerBinding, BottomDrawerViewModel, BottomDrawerViewController>(),
    MenuItemClickListener {

    override val mViewController = BottomDrawerViewController()
    override val mViewModel: BottomDrawerViewModel by viewModels()

    override val mBindingInflater: ((LayoutInflater, ViewGroup?, Boolean) -> FragmentBottomDrawerBinding)
        get() = FragmentBottomDrawerBinding::inflate

    val isDrawerHidden: Boolean
        get() = mViewController.isDrawerHidden

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewController.setArgumentsViewDependsOn(
            menuItemsAdapter = mViewModel.menuItemsAdapter,
            scrimVisibility = mViewModel.scrimVisibilityState
        )
        mViewModel.menuItemsAdapter.setOnDrawerMenuClickListener(this)
        mViewController.viewBinding.viewScrim.setOnClickListener {
            closeDrawer()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateMenuItems() }
            launch { collectSharedLogOut() }
        }
    }

    override fun onMenuItemClicked(item: MenuItem) {
        mViewController.onMenuItemClicked(
            currentFragment = this,
            item = item,
            onLogOut = { mViewModel.onLogOut() }
        )
    }

    override fun onStop() {
        mViewModel.scrimVisibilityState = mViewController.viewBinding.viewScrim.visibility
        super.onStop()
    }

    fun addOnSlideAction(action: OnSlideAction) {
        mViewController.addOnSlideAction(action)
    }

    fun openDrawer() {
        mViewController.openDrawer()
    }

    fun closeDrawer() {
        mViewController.closeDrawer()
    }

    fun handleOnBackPressed(): Boolean {
        return mViewController.handleOnBackPressed()
    }

    private suspend fun collectStateMenuItems() {
        mViewModel.stateMenuItems.collect { items ->
            withContext(mCoroutineContext.Main) {
                mViewController.onMenuItemsPrepared(items)
            }
        }
    }

    private suspend fun collectSharedLogOut() {
        mViewModel.sharedLogOut.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onLogOut()
            }
        }
    }
}