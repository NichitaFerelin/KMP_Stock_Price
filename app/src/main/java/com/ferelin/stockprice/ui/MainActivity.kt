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
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractorImpl
import com.ferelin.stockprice.databinding.ActivityMainBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.ui.bottomDrawerSection.BottomDrawerFragment
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions.ArrowUpAction
import com.ferelin.stockprice.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    @Inject
    lateinit var dataInteractor: DataInteractorImpl

    val navigator: Navigator by lazy(LazyThreadSafetyMode.NONE) { Navigator(this) }

    private val mViewModel: MainViewModel by viewModels()
    private var mViewBinding: ActivityMainBinding? = null
    val root: ViewGroup
        get() = mViewBinding!!.root

    private val mBottomNavDrawer: BottomDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottomNavFragment) as BottomDrawerFragment
    }

    private var mMessagesForServiceCollectorJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)

        if (savedInstanceState == null) {
            hideBottomBar()
            mViewBinding!!.bottomAppBar.visibility = View.GONE
            mViewBinding!!.mainFab.visibility = View.GONE
        }

        initObservers()
        setUpViewComponents()

        if (savedInstanceState == null) {
            navigator.navigateToLoadingFragment()
        }

        lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.isUserLogged
                .filter { it != null }
                .collect { isLogged ->
                    withContext(mCoroutineContext.Main) {
                        if (isLogged!!) {
                            mViewBinding!!.mainFab.setImageResource(R.drawable.ic_user_photo)
                        } else {
                            mViewBinding!!.mainFab.setImageResource(R.drawable.ic_key)
                        }
                    }
                }
        }

        mViewBinding!!.mainFab.setOnClickListener {
            lifecycleScope.launch(mCoroutineContext.IO) {
                mViewModel.isUserLogged
                    .filter { it != null }
                    .collect {
                        if (it!!) {

                        } else {
                            withContext(mCoroutineContext.Main) {
                                navigator.navigateToLoginFragment()
                            }
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel.isServiceRunning) {
            mMessagesForServiceCollectorJob?.cancel()
            StockObserverController.stopService(this@MainActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            collectMessagesForService()
        }
    }

    override fun onDestroy() {
        saveState()
        StockObserverController.stopService(this@MainActivity)
        mViewBinding = null
        super.onDestroy()
    }

    fun hideBottomBar() {
        with(mViewBinding!!) {
            mainFab.hide()
            bottomAppBar.performHide()
        }
    }

    fun showBottomBar() {
        with(mViewBinding!!) {
            if (bottomAppBar.visibility != View.VISIBLE) {
                bottomAppBar.visibility = View.VISIBLE
                mainFab.visibility = View.VISIBLE
            }

            bottomAppBar.performShow()
            mainFab.show()
        }
    }

    fun handleOnBackPressed(): Boolean {
        return mBottomNavDrawer.handleOnBackPressed()
    }

    private fun saveState() {
        mViewModel.arrowState = if (mViewBinding!!.bottomAppBarImageViewArrowUp.rotation > 90F) {
            180F
        } else 0F
    }

    private fun onControlButtonPressed() {
        if (mBottomNavDrawer.isDrawerHidden) {
            mBottomNavDrawer.openDrawer()
            mViewBinding!!.mainFab.hide()
        } else {
            mViewBinding!!.mainFab.show()
            mBottomNavDrawer.closeDrawer()
        }
    }

    private fun injectDependencies() {
        if (application is App) {
            (application as App).run {
                appComponent.inject(this@MainActivity)
                appComponent.inject(mViewModel)
            }
        }
    }

    fun showFab() {
        mViewBinding!!.mainFab.show()
    }

    fun hideFab() {
        mViewBinding!!.mainFab.hide()
    }

    private fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
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

    private fun collectMessagesForService() {
        mMessagesForServiceCollectorJob = lifecycleScope.launch(mCoroutineContext.IO) {
            mViewModel.eventObserverCompanyChanged.collect {
                when {
                    !isActive -> cancel()
                    it == null -> {
                        if (mViewModel.isServiceRunning) {
                            StockObserverController.stopService(this@MainActivity)
                        }
                    }
                    else -> {
                        StockObserverController.updateService(this@MainActivity, it)
                        mViewModel.isServiceRunning = true
                    }
                }
            }
        }
    }

    private fun setUpViewComponents() {
        setStatusBarColor()

        mViewBinding!!.run {
            bottomAppBarImageViewArrowUp.rotation = mViewModel.arrowState
            bottomAppBarLinearRoot.setOnClickListener { onControlButtonPressed() }
            mBottomNavDrawer.addOnSlideAction(
                ArrowUpAction(bottomAppBarImageViewArrowUp)
            )
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}