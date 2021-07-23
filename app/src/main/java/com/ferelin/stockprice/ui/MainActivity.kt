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
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.ActivityMainBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.ui.bottomDrawerSection.bottomBar.BottomBarFragment
import com.ferelin.stockprice.utils.showDefaultDialog
import com.ferelin.stockprice.utils.showDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    private val mViewModel: MainViewModel by viewModels()
    private var mViewBinding: ActivityMainBinding? = null

    private var mBottomBar: BottomBarFragment? = null

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

        setStatusBarColor()
        initObservers()
        mViewModel.initObservers()
        findBottomBar()

        if (savedInstanceState == null) {
            navigator.navigateToLoadingFragment()
        }
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
        if (isFinishing) {
            mStockObserverController.onActivityFinishingDestroy(this)
        }

        navigator.detachHostActivity()
        mBottomBar = null
        mViewBinding = null
        super.onDestroy()
    }

    fun handleOnBackPressed(): Boolean {
        return mBottomBar?.handleOnBackPressed() ?: false
    }

    fun hideBottomBar() {
        mBottomBar?.hideBottomBar()
    }

    fun showBottomBar() {
        mBottomBar?.showBottomBar()
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

    private fun findBottomBar() {
        mBottomBar = supportFragmentManager
            .findFragmentById(R.id.bottomBarFragment) as BottomBarFragment
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}