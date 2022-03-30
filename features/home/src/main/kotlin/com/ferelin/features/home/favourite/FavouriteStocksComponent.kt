package com.ferelin.features.home.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class FavouriteStocksScope

@FavouriteStocksScope
@Component(dependencies = [FavouriteStocksDeps::class])
internal interface FavouriteStocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: FavouriteStocksDeps): Builder
    fun build(): FavouriteStocksComponent
  }

  fun viewModelFactory(): FavouriteStocksViewModelFactory
}

interface FavouriteStocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}

internal class FavouriteStocksComponentViewModel(deps: FavouriteStocksDeps) : ViewModel() {
  val component = DaggerFavouriteStocksComponent.builder()
    .dependencies(deps)
    .build()
}

internal class FavouriteStocksComponentViewModelFactory constructor(
  private val deps: FavouriteStocksDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == FavouriteStocksComponentViewModel::class.java)
    return FavouriteStocksComponentViewModel(deps) as T
  }
}