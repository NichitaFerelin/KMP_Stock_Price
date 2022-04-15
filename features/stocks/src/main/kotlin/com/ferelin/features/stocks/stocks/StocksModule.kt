package com.ferelin.features.stocks.stocks

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val stocksModule = module {
    viewModel { StocksViewModel(get(), get()) }
}