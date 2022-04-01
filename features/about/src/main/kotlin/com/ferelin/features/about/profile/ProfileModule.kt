package com.ferelin.features.about.profile

import com.ferelin.core.ui.params.ProfileParams
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
  viewModel { params ->
    ProfileViewModel(
      get(parameters = { params }),
      get(),
      get(),
      get()
    )
  }

  factory { params ->
    ProfileParams(
      companyId = params.get()
    )
  }
}
