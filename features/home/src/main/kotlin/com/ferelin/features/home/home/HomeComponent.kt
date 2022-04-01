package com.ferelin.features.home.home

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
  viewModel { HomeViewModel() }
}