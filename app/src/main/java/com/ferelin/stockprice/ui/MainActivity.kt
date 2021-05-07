package com.ferelin.stockprice.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.services.observer.StockObserverController
import com.ferelin.stockprice.utils.showDialog
import com.ferelin.stockprice.viewModelFactories.ApplicationViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    private lateinit var mViewModel: MainViewModel

    private var mMessagesForServiceCollectorJob: Job? = null

    val dataInteractor: DataInteractor
        get() = (application as App).dataInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpComponents()
        initObservers()
        Navigator.navigateToLoadingFragment(this)
    }

    override fun onResume() {
        super.onResume()
        mMessagesForServiceCollectorJob?.cancel()
        StockObserverController.stopService(this@MainActivity)
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
                    it == null -> StockObserverController.stopService(this@MainActivity)
                    else -> StockObserverController.updateService(this@MainActivity, it)
                }
            }
        }
    }

    private fun setUpComponents() {
        val factory = ApplicationViewModelFactory(mCoroutineContext, dataInteractor, application)
        mViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        setStatusBarColor()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}