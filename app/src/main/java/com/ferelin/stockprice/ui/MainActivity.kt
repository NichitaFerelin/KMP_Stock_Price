package com.ferelin.stockprice.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.stocksPager.StocksPagerFragment
import com.ferelin.stockprice.viewModelFactories.AndroidViewModelFactory
import kotlinx.coroutines.FlowPreview

class MainActivity(
    private val mContextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : AppCompatActivity() {

    @FlowPreview
    private val mViewModel: MainViewModel by viewModels {
        AndroidViewModelFactory(mContextProvider, dataInteractor, application)
    }

    val dataInteractor: DataInteractor
        get() = (application as App).dataInteractor

    @FlowPreview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }

        mViewModel

        with(supportFragmentManager) {
            findFragmentByTag("StocksFragment") ?: commit {
                replace(R.id.fragmentContainer, StocksPagerFragment(), "StocksFragment")
            }
        }
    }
}