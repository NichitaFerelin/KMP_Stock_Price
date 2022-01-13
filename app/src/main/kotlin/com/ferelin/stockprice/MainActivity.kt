package com.ferelin.stockprice

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.stockprice.navigation.AppNavigationGraph

internal class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { StockPriceApp() }
  }

  @Composable
  private fun StockPriceApp() {
    AppTheme {
      val navController = rememberNavController()
      AppNavigationGraph(
        navHostController = navController,
        appComponent = (application as App).appComponent
      )
    }
  }
}