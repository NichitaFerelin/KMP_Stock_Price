package com.ferelin.stockprice.desktopApp

import androidx.compose.ui.window.application
import com.ferelin.stockprice.shared.initKoin
import org.koin.core.module.Module

fun main() = application {
  startKoin()
  MainWindow()
}

private fun startKoin() {
  initKoin {
    modules(koinModules)
  }
}

private val koinModules = listOf<Module>()