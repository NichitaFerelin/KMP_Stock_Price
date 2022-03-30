package com.ferelin.features.home.stocks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StocksScope

@StocksScope
@Component(dependencies = [StocksDeps::class])
internal interface StocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: StocksDeps): Builder
    fun build(): StocksComponent
  }

  fun viewModelFactory(): StocksViewModelFactory
}

interface StocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}

internal class StocksComponentViewModel(deps: StocksDeps) : ViewModel() {
  val component = DaggerStocksComponent.builder()
    .dependencies(deps)
    .build()
}

internal class StocksComponentViewModelFactory constructor(
  private val deps: StocksDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == StocksComponentViewModel::class.java)
    return StocksComponentViewModel(deps) as T
  }
}