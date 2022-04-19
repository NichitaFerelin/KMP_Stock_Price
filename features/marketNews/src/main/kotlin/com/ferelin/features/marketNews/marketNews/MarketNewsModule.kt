package com.ferelin.features.marketNews.marketNews

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val marketNewsModule = module {
    viewModel { MarketNewsViewModel(get(), get(), get()) }
}