package com.ferelin.stockprice.shared.commonMain.ui.di

import com.ferelin.stockprice.shared.commonMain.ui.DispatchersProvider
import com.ferelin.stockprice.shared.commonMain.ui.params.AboutParams
import com.ferelin.stockprice.shared.commonMain.ui.params.NewsParams
import com.ferelin.stockprice.shared.commonMain.ui.params.ProfileParams
import com.ferelin.stockprice.shared.commonMain.ui.viewModel.*
import com.ferelin.stockprice.ui.viewModel.*
import org.koin.dsl.module

val viewModelModule = module {
  factory { DispatchersProvider() }

  factory { params ->
    AboutParams(
      companyId = params.get(),
      companyTicker = params.get(),
      companyName = params.get()
    )
  }
  factory { params ->
    NewsParams(
      companyId = params.get(),
      companyTicker = params.get()
    )
  }
  factory { params ->
    ProfileViewModel(
      get(parameters = { params }),
      get(),
      get(),
      viewModelScope = params.get(),
      get()
    )
  }
  factory { params ->
    CryptosViewModel(get(), viewModelScope = params.get(), get(), get())
  }
  factory { params ->
    FavouriteStocksViewModel(get(), get(), viewModelScope = params.get(), get())
  }
  factory { params ->
    LoginViewModel(viewModelScope = params.get(), get())
  }
  factory { params ->
    SearchViewModel(get(), get(), get(), viewModelScope = params.get(), get())
  }
  factory { params ->
    SettingsViewModel(get(), get(), viewModelScope = params.get(), get())
  }
  factory { params ->
    StocksViewModel(get(), get(), viewModelScope = params.get(), get())
  }
  factory { HomeViewModel() }

  factory { params ->
    AboutViewModel(
      get(parameters = { params }),
      get(),
      viewModelScope = params.get(),
      get()
    )
  }
  factory { params ->
    NewsViewModel(
      get(parameters = { params }),
      get(),
      viewModelScope = params.get(),
      get()
    )
  }
  factory { params ->
    ProfileParams(
      companyId = params.get()
    )
  }
}