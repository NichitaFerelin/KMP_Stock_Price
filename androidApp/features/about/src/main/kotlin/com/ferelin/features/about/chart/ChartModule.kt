package com.ferelin.features.about.chart

import com.ferelin.core.ui.params.ChartParams
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chartModule = module {
  viewModel { params ->
    ChartViewModel(
      get(parameters = { params }),
      get(),
      get(),
      get(),
      get()
    )
  }

  factory { params ->
    ChartParams(
      companyId = params.get(),
      companyTicker = params.get()
    )
  }
}