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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentBottomBarBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.BottomDrawerFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions.ArrowUpAction
import com.ferelin.stockprice.utils.bottomDrawer.OnStateAction
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomBarFragment :
    BaseFragment<FragmentBottomBarBinding, BottomBarViewModel, BottomBarViewController>() {

    override val mViewController = BottomBarViewController()
    override val mViewModel: BottomBarViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBottomBarBinding
        get() = FragmentBottomBarBinding::inflate

    private val mBottomNavDrawer: BottomDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.bottomNavFragment) as BottomDrawerFragment
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpViewComponents()
        mViewController.setArgumentsViewDependsOn(
            mViewModel.isBottomBarVisible,
            mViewModel.isBottomBarFabVisible,
            mViewModel.arrowState
        )
    }

    override fun onDestroyView() {
        saveState()
        super.onDestroyView()
    }

    fun handleOnBackPressed() : Boolean {
        return mBottomNavDrawer.handleOnBackPressed()
    }

    fun hideBottomBar() {
        mViewController.hideBottomBar()
    }

    fun showBottomBar() {
        mViewController.showBottomBar()
    }

    private fun setUpViewComponents() {
        setUpBottomBar()
        setUpFab()
    }

    private fun onControlButtonPressed() {
        if (mBottomNavDrawer.isDrawerHidden) {
            mBottomNavDrawer.openDrawer()
        } else mBottomNavDrawer.closeDrawer()
    }

    private fun setUpBottomBar() {
        mViewController.viewBinding.run {
            bottomAppBarLinearRoot.setOnClickListener { onControlButtonPressed() }
            mBottomNavDrawer.addOnSlideAction(ArrowUpAction(bottomAppBarImageViewArrowUp))
            mBottomNavDrawer.addOnStateAction(object : OnStateAction {
                override fun onBottomDrawerStateChanged(newState: Int) {
                    mViewController.onBottomDrawerStateChanged(newState)
                }
            })
        }
    }

    private fun setUpFab() {
        setUpFabIcon()

        mViewController.viewBinding.mainFab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
                val isUserAuthenticated = mViewModel.stateIsUserAuthenticated.firstOrNull() ?: false
                withContext(mCoroutineContext.Main) {
                    mViewController.onFabClicked(isUserAuthenticated)
                }
            }
        }
    }

    private fun setUpFabIcon() {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.stateIsUserAuthenticated.collect { isAuthenticated ->
                withContext(mCoroutineContext.Main) {
                    mViewController.onAuthenticationStateChanged(isAuthenticated)
                }
            }
        }
    }

    private fun saveState() {
        with(mViewController.viewBinding) {
            mViewModel.arrowState = if (bottomAppBarImageViewArrowUp.rotation > 90F) {
                180F
            } else 0F

            mViewModel.isBottomBarFabVisible = mainFab.isVisible
            mViewModel.isBottomBarVisible = bottomAppBar.isVisible
        }
    }
}