package com.ferelin.stockprice.desktopApp

import androidx.compose.ui.window.application
import com.ferelin.stockprice.shared.initKoin

fun main() = application {
  initKoin()
  MainWindow()
}