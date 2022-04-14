package com.ferelin.features.about.about

import com.ferelin.core.ui.params.AboutParams
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val aboutModule = module {
  viewModel { params ->
    AboutViewModel(
      get(parameters = { params }),
      get(),
    )
  }

  factory { params ->
    AboutParams(
      companyId = params.get(),
      companyTicker = params.get(),
      companyName = params.get()
    )
  }
}