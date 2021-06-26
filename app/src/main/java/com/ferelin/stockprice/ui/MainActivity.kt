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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.onSlide.ArrowUpAction
import com.ferelin.stockprice.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    @Inject
    lateinit var dataInteractor: DataInteractorImpl

    private val mViewModel: MainViewModel by viewModels()
    private var mViewBinding: ActivityMainBinding? = null
    val root: ViewGroup
        get() = mViewBinding!!.root

    private val mBottomNavDrawer: BottomDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottomNavFragment) as BottomDrawerFragment
    }

    private var mMessagesForServiceCollectorJob: Job? = null

    private lateinit var mSlideToBottom: Animation
    private lateinit var mSlideToTop: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)

        if (savedInstanceState == null) {
            // TODO. Migrates to navigation component
            hideBottomBar()
            mViewBinding!!.bottomAppBar.visibility = View.GONE
            mViewBinding!!.fab.visibility = View.GONE
        }

        initObservers()
        setUpViewComponents()

        if (savedInstanceState == null) {
            Navigator.navigateToLoadingFragment(this)
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
        stopAnimations()
        StockObserverController.stopService(this@MainActivity)
        mViewBinding = null
        super.onDestroy()
    }

    fun hideBottomBar() {
        with(mViewBinding!!) {
            fab.hide()
            bottomAppBar.performHide()
        }
    }

    fun showBottomBar() {
        with(mViewBinding!!) {
            if(bottomAppBar.visibility != View.VISIBLE) {
                bottomAppBar.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE
            }

            bottomAppBar.performShow()
            fab.show()
        }
    }

    fun handleOnBackPressed(): Boolean {
        return mBottomNavDrawer.handleOnBackPressed()
    }

    private fun stopAnimations() {
        mSlideToBottom.invalidate()
        mSlideToTop.invalidate()
    }

    private fun saveState() {
        mViewModel.arrowState = if (mViewBinding!!.bottomAppBarImageViewArrowUp.rotation > 90F) {
            180F
        } else 0F
    }

    private fun onControlButtonPressed() {
        if (mBottomNavDrawer.isDrawerHidden) {
            mBottomNavDrawer.openDrawer()
            mViewBinding!!.fab.hide()
        } else {
            mBottomNavDrawer.closeDrawer()
            mViewBinding!!.fab.show()
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

        mSlideToBottom = AnimationUtils.loadAnimation(this, R.anim.slide_bottom)
        mSlideToTop = AnimationUtils.loadAnimation(this, R.anim.slide_top)

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