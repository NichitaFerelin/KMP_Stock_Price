package com.ferelin.features.settings

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
  viewModel {
    SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
  }
}