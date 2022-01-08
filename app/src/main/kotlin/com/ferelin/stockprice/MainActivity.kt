package com.ferelin.stockprice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ferelin.core.ui.view.routing.RouterHost
import com.ferelin.stockprice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RouterHost {
  override var host: AppCompatActivity? = null
  override val containerViewId: Int = R.id.container

  private var viewBinding: ActivityMainBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    application.let {
      if (it is App) it.appComponent.inject(this)
    }
    ActivityMainBinding.inflate(layoutInflater).also {
      viewBinding = it
      setContentView(it.root)
    }
    host = this
  }

  override fun onDestroy() {
    viewBinding = null
    host = null
    super.onDestroy()
  }
}