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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.utils.showDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    private val mViewModel: MainViewModel by viewModels()

    private var mMessagesForServiceCollectorJob: Job? = null

    @Inject
    lateinit var dataInteractor: DataInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpComponents()
        initObservers()
        Navigator.navigateToLoadingFragment(this)
    }

    private fun injectDependencies() {
        if (application is App) {
            (application as App).run {
                appComponent.inject(this@MainActivity)
                appComponent.inject(mViewModel)
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
        StockObserverController.stopService(this@MainActivity)
        super.onDestroy()
    }

    private fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectEventCriticalError() }
            launch { collectNetworkState() }
            launch { collectEventApiLimitError() }
        }
    }

    private suspend fun collectEventCriticalError() {
        mViewModel.eventCriticalError.collect { showDialog(it, supportFragmentManager) }
    }

    private suspend fun collectNetworkState() {
        mViewModel.stateIsNetworkAvailable.collect { isAvailable ->
            if (!isAvailable) {
                withContext(mCoroutineContext.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.errorNetwork,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private suspend fun collectEventApiLimitError() {
        mViewModel.eventApiLimitError.collect { message ->
            withContext(mCoroutineContext.Main) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        }
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

    private fun setUpComponents() {
        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}