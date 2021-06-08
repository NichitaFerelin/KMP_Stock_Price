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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.common.menu.MenuItemClickListener
import com.ferelin.stockprice.databinding.FragmentMenuBinding
import com.ferelin.stockprice.viewModelFactories.DataViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel, MenuViewController>(),
    MenuItemClickListener {

    override val mViewController: MenuViewController = MenuViewController()
    override val mViewModel: MenuViewModel by viewModels {
        DataViewModelFactory(mCoroutineContext, mDataInteractor)
    }

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMenuBinding
        get() = FragmentMenuBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpClickListeners()
        mViewController.setArgumentsViewDependsOn(mViewModel.menuItemsAdapter)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateMenuItems() }
            launch { collectSharedLogOut() }
        }
    }

    override fun onMenuItemClicked(item: MenuItem) {
        mViewController.onMenuItemClicked(this, item, onLogOut = {
            mViewModel.onLogOut()
        })
    }

    private fun setUpClickListeners() {
        mViewModel.menuItemsAdapter.setOnDrawerMenuClickListener(this)
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