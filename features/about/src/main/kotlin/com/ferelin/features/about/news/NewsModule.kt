package com.ferelin.features.about.news

import com.ferelin.core.ui.params.NewsParams
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newsModule = module {
  viewModel { params ->
    NewsViewModel(
      get(parameters = { params }),
      get(),
      get(),
      get()
    )
  }

  factory { params ->
    NewsParams(
      companyId = params.get(),
      companyTicker = params.get()
    )
  }
}