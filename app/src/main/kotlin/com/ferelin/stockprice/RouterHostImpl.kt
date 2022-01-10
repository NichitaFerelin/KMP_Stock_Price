package com.ferelin.stockprice

import androidx.appcompat.app.AppCompatActivity
import com.ferelin.core.ui.view.routing.RouterHost
import javax.inject.Inject

internal class RouterHostImpl @Inject constructor() : RouterHost {
  override var host: AppCompatActivity? = null
  override val containerViewId: Int = R.id.container
}