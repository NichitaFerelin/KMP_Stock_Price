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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentMenuBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.login.LoginFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItemClickListener
import com.ferelin.stockprice.ui.bottomDrawerSection.register.RegisterFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel, MenuViewController>(),
    MenuItemClickListener {

    override val mViewController = MenuViewController()
    override val mViewModel: MenuViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMenuBinding
        get() = FragmentMenuBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpClickListeners()
        mViewController.setArgumentsViewDependsOn(
            menuItemsAdapter = mViewModel.menuItemsAdapter,
            isWaitingForLoginResult = mViewModel.isWaitingForLoginResult,
            isWaitingForRegisterResult = mViewModel.isWaitingForRegisterResult,
            isUserAuthenticated = mViewModel.isUserAuthenticated
        )

        setFragmentResultListener(LoginFragment.LOGIN_REQUEST_KEY) { _, bundle ->
            mViewController.onLoginResult(
                this,
                bundle,
                mViewModel.stateIsUserRegister.value ?: false
            )
            mViewModel.isWaitingForLoginResult = false
        }

        setFragmentResultListener(RegisterFragment.REGISTER_REQUEST_KEY) { _, bundle ->
            mViewController.onRegisterResult(this, bundle)
            mViewModel.isWaitingForRegisterResult = false
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
            isUserAuthenticated = mViewModel.isUserAuthenticated,
            isUserRegistered = mViewModel.stateIsUserRegister.value ?: false,
            onLogOut = { mViewModel.onLogOut() }
        )
    }

    fun onWaitingForLoginResult() {
        mViewModel.isWaitingForLoginResult = true
    }

    fun onWaitingForRegisterResult() {
        mViewModel.isWaitingForRegisterResult = true
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