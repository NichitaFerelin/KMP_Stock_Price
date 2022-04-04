package com.ferelin.features.home.favourite

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favouriteStocksModule = module {
  viewModel { FavouriteStocksViewModel(get(), get(), get()) }
}