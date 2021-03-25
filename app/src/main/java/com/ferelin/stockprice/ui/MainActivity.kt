package com.ferelin.stockprice.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.utils.showDialog
import com.ferelin.stockprice.viewModelFactories.ApplicationViewModelFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
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

        val factory = ApplicationViewModelFactory(mCoroutineContext, dataInteractor, application)
        mViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)
        initObservers()
        setStatusBarColor()

        lifecycleScope.launch(mCoroutineContext.IO) {
            with(supportFragmentManager) {
                val tag = "StocksFragment"
                findFragmentByTag(tag) ?: commit {
                    add(R.id.fragmentContainer, StocksPagerFragment(), tag)
                }
            }
        }
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
                mViewModel.actionNetworkError.collect {
                    withContext(mCoroutineContext.Main) {
                        Toast.makeText(this@MainActivity, R.string.errorNetwork, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
}