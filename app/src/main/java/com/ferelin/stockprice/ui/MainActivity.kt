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
import com.ferelin.stockprice.utils.showDialog
import com.ferelin.stockprice.viewModelFactories.ApplicationViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    @FlowPreview
    private lateinit var mViewModel: MainViewModel

    val dataInteractor: DataInteractor
        get() = (application as App).dataInteractor

    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = ApplicationViewModelFactory(mCoroutineContext, dataInteractor, application)
        mViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        initObservers()
        setStatusBarColor()
        Navigator.navigateToLoadingFragment(this)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }

    @FlowPreview
    private fun initObservers() {
        lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.actionShowDialogError.collect {
                    showDialog(it, supportFragmentManager)
                }
            }
            launch {
                mViewModel.actionShowNetworkError
                    .filter { it }
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                R.string.errorNetwork,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
            }
            launch {
                mViewModel.actionShowApiLimitError.collect {
                    withContext(mCoroutineContext.Main) {
                        Toast.makeText(this@MainActivity, R.string.errorApiLimit, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}