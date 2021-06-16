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
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractorImpl
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.utils.showDefaultDialog
import com.ferelin.stockprice.utils.showDialog
import com.ferelin.stockprice.utils.withTimerOnUi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    private val mViewModel: MainViewModel by viewModels()

    private var mMessagesForServiceCollectorJob: Job? = null

    @Inject
    lateinit var dataInteractor: DataInteractorImpl

    private var mTextViewError: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpComponents()
        initObservers()
        Navigator.navigateToLoadingFragment(this)
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
        mTextViewError = null
        StockObserverController.stopService(this@MainActivity)
        super.onDestroy()
    }

    private fun injectDependencies() {
        if (application is App) {
            (application as App).run {
                appComponent.inject(this@MainActivity)
                appComponent.inject(mViewModel)
            }
        }
    }

    private fun onError(text: String) {
        mTextViewError!!.text = text
        showError()
        withTimerOnUi(4000L) { hideError() }
    }

    private fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectNetworkState() }
            launch { collectEventCriticalError() }
            launch { collectEventApiLimitError() }
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

    private suspend fun collectNetworkState() {
        mViewModel.stateIsNetworkAvailable.collect { isAvailable ->
            if (!isAvailable) {
                withContext(mCoroutineContext.Main) {
                    onError(getString(R.string.errorNetwork))
                }
            }
        }
    }

    private suspend fun collectEventApiLimitError() {
        mViewModel.eventApiLimitError.collect { message ->
            withContext(mCoroutineContext.Main) {
                onError(message)
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
        mTextViewError = findViewById(R.id.textViewError)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }

    private fun showError() {
        val root = findViewById<ConstraintLayout>(R.id.mainRoot)
        ConstraintSet().apply {
            clone(root)
            clear(R.id.textViewError, ConstraintSet.TOP)
            connect(R.id.textViewError, ConstraintSet.BOTTOM, R.id.mainRoot, ConstraintSet.BOTTOM)
            applyTo(root)
        }
    }

    private fun hideError() {
        val root = findViewById<ConstraintLayout>(R.id.mainRoot)
        ConstraintSet().apply {
            clone(root)
            clear(R.id.textViewError, ConstraintSet.BOTTOM)
            connect(R.id.textViewError, ConstraintSet.TOP, R.id.mainRoot, ConstraintSet.BOTTOM)
            applyTo(root)
        }
    }
}