package com.ferelin.stockprice.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.App
import com.ferelin.stockprice.R
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksPager.StocksPagerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val dataInteractor: DataInteractor
        get() = (application as App).dataInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch(Dispatchers.IO) {
            dataInteractor.prepareData(this@MainActivity)
        }

        with(supportFragmentManager) {
            findFragmentByTag("StocksFragment") ?: commit {
                replace(R.id.fragmentContainer, StocksPagerFragment(), "StocksFragment")
            }
        }
    }
}