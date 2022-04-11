package com.ferelin.stockprice.shared.ui.di

import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.viewModel.*
import org.koin.dsl.module

val viewModelModule = module {
    factory { DispatchersProvider() }

    factory { HomeViewModel() }
    factory { params ->
        CryptosViewModel(get(), viewModelScope = params.get(), get(), get())
    }
    factory { params ->
        FavouriteStocksViewModel(get(), get(), viewModelScope = params.get(), get())
    }
    factory { params ->
        SearchViewModel(get(), get(), get(), viewModelScope = params.get(), get())
    }
    factory { params ->
        StocksViewModel(get(), get(), viewModelScope = params.get(), get())
    }

    factory { params ->
        AboutViewModel(
            aboutParams = params.get(),
            get(),
            viewModelScope = params.get(),
            get()
        )
    }
    factory { params ->
        ProfileViewModel(
            profileParams = params.get(),
            get(),
            get(),
            viewModelScope = params.get(),
            get()
        )
    }
    factory { params ->
        NewsViewModel(
            newsParams = params.get(),
            get(),
            viewModelScope = params.get(),
            get()
        )
    }
}