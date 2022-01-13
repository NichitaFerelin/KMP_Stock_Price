package com.ferelin.stockprice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injectDeps()
  }

  private fun injectDeps() {
    application.let { app ->
      if (app is App) {
        app.appComponent.inject(this)
      }
    }
  }
}