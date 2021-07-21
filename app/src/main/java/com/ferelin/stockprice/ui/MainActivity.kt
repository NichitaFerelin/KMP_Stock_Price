package com.ferelin.stockprice.ui

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

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.ActivityMainBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.ui.bottomDrawerSection.BottomDrawerFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions.ArrowUpAction
import com.ferelin.stockprice.utils.bottomDrawer.OnStateAction
import com.ferelin.stockprice.utils.showDefaultDialog
import com.ferelin.stockprice.utils.showDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    private val mViewModel: MainViewModel by viewModels()
    private var mViewBinding: ActivityMainBinding? = null

    private val mBottomNavDrawer: BottomDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottomNavFragment) as BottomDrawerFragment
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var mStockObserverController: StockObserverController

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)
        navigator.attachHostActivity(this)

        initObservers()
        setUpViewComponents()
        mViewModel.initObservers()

        if (savedInstanceState == null) {
            hideBottomBar()
            navigator.navigateToLoadingFragment()
        } else restoreState()
    }

    override fun onResume() {
        super.onResume()
        mStockObserverController.onActivityResume(this)
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            mStockObserverController.onActivityNotFinishingPause()
        }
    }

    override fun onDestroy() {
        saveState()

        if (isFinishing) {
            mStockObserverController.onActivityFinishingDestroy(this)
        }

        navigator.detachHostActivity()
        mViewBinding = null
        super.onDestroy()
    }

    fun hideBottomBar() {
        with(mViewBinding!!) {
            if (bottomAppBar.isVisible) {
                bottomAppBar.isVisible = false
            }

            mainFab.hide()
            bottomAppBar.performHide()
        }
    }

    fun showBottomBar() {
        with(mViewBinding!!) {
            if (!bottomAppBar.isVisible) {
                bottomAppBar.isVisible = true
            }

            mainFab.show()
            bottomAppBar.performShow()
        }
    }

    fun handleOnBackPressed(): Boolean {
        return mBottomNavDrawer.handleOnBackPressed()
    }

    private fun saveState() {
        mViewModel.arrowState = if (mViewBinding!!.bottomAppBarImageViewArrowUp.rotation > 90F) {
            180F
        } else 0F

        mViewModel.isBottomBarFabVisible = mViewBinding!!.mainFab.isVisible
        mViewModel.isBottomBarVisible = mViewBinding!!.bottomAppBar.isVisible
    }

    private fun onControlButtonPressed() {
        if (mBottomNavDrawer.isDrawerHidden) {
            mBottomNavDrawer.openDrawer()
        } else mBottomNavDrawer.closeDrawer()
    }

    private fun injectDependencies() {
        val application = application
        if (application is App) {
            application.appComponent.inject(this)
            application.appComponent.inject(mViewModel)
        }
    }

    private fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            launch { mViewModel.stateIsNetworkAvailable.collect() }
            launch { collectEventCriticalError() }
            launch { collectFavouriteCompaniesLimitError() }
        }
    }

    private suspend fun collectFavouriteCompaniesLimitError() {
        mViewModel.eventOnFavouriteCompaniesLimitError.collect {
            withContext(mCoroutineContext.Main) {
                showDefaultDialog(this@MainActivity, it)
            }
        }
    }

    private suspend fun collectEventCriticalError() {
        mViewModel.eventCriticalError.collect { showDialog(it, supportFragmentManager) }
    }

    private fun setUpViewComponents() {
        setStatusBarColor()
        setUpFab()

        mViewBinding!!.run {
            bottomAppBarImageViewArrowUp.rotation = mViewModel.arrowState
            bottomAppBarLinearRoot.setOnClickListener { onControlButtonPressed() }
            mBottomNavDrawer.addOnSlideAction(ArrowUpAction(bottomAppBarImageViewArrowUp))
            mBottomNavDrawer.addOnStateAction(object : OnStateAction {
                override fun onBottomDrawerStateChanged(newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN && bottomAppBar.isVisible) {
                        mViewBinding!!.mainFab.show()
                    } else if (newState != BottomSheetBehavior.STATE_HIDDEN) {
                        mViewBinding!!.mainFab.hide()
                    }
                }
            })
        }
    }

    private fun setUpFab() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            collectStateUserAuthenticated()
        }

        mViewBinding!!.mainFab.setOnClickListener {
            lifecycleScope.launch(mCoroutineContext.IO) {
                val isUserAuthenticated = mViewModel.stateIsUserAuthenticated.firstOrNull() ?: false
                if (!isUserAuthenticated) {
                    withContext(mCoroutineContext.Main) {
                        navigator.navigateToLoginFragment(false)
                    }
                }
            }
        }
    }

    private suspend fun collectStateUserAuthenticated() {
        mViewModel.stateIsUserAuthenticated.collect { isLogged ->
            withContext(mCoroutineContext.Main) {
                /**
                 * Changes the icon depending on the authentication state.
                 * */
                if (isLogged) {
                    mViewBinding!!.mainFab.setImageResource(R.drawable.ic_user_photo)
                    mViewBinding!!.mainFab.contentDescription =
                        getString(R.string.descriptionFabProfile)
                } else {
                    mViewBinding!!.mainFab.setImageResource(R.drawable.ic_key)
                    mViewBinding!!.mainFab.contentDescription =
                        getString(R.string.descriptionFabLogIn)
                }
            }
        }
    }

    private fun restoreState() {
        if (mViewModel.isBottomBarFabVisible) {
            mViewBinding!!.mainFab.show()
        } else mViewBinding!!.mainFab.hide()

        if (mViewModel.isBottomBarVisible) {
            showBottomBar()
        } else hideBottomBar()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}