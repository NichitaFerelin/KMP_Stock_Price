package com.ferelin.features.home.stocks

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val stocksModule = module {
  viewModel { StocksViewModel(get(), get(), get()) }
}