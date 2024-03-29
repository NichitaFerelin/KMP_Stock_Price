package com.ferelin.stockprice.androidApp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ferelin.stockprice.androidApp.ui.navigation.AppNavigationGraph
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import com.google.accompanist.insets.ProvideWindowInsets

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StockPriceApp() }
    }

    @Composable
    private fun StockPriceApp() {
        AppTheme {
            ProvideWindowInsets(consumeWindowInsets = true) {
                val navController = rememberNavController()
                AppNavigationGraph(
                    navHostController = navController
                )
            }
        }
    }
}