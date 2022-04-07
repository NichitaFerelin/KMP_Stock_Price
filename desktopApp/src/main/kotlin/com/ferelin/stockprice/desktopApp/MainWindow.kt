package com.ferelin.stockprice.desktopApp

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.ferelin.stockprice.desktopApp.navigation.AppNavigationGraph
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun ApplicationScope.MainWindow() {
  Window(
    onCloseRequest = ::exitApplication,
    title = "Stock Price",
    state = rememberWindowState(width = 1400.dp, height = 600.dp)
  ) {
    AppTheme {
      AppNavigationGraph()
    }
  }
}