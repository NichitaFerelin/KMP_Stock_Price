package com.ferelin.features.cryptos.cryptos

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cryptosModule = module {
    viewModel { CryptosViewModel(get(), get(), get(), get()) }
}