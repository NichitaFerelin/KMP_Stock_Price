package com.ferelin.features.stocks.defaults

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DefaultStocksScope

@DefaultStocksScope
@Component(dependencies = [DefaultStocksDeps::class])
internal interface DefaultStocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: DefaultStocksDeps): Builder
    fun build(): DefaultStocksComponent
  }

  fun viewModelFactory(): DefaultStocksViewModelFactory
}

interface DefaultStocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}

internal class DefaultStocksComponentViewModel(deps: DefaultStocksDeps) : ViewModel() {
  val component = DaggerDefaultStocksComponent.builder()
    .dependencies(deps)
    .build()
}

internal class DefaultStocksComponentViewModelFactory constructor(
  private val deps: DefaultStocksDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == DefaultStocksComponentViewModel::class.java)
    return DefaultStocksComponentViewModel(deps) as T
  }
}