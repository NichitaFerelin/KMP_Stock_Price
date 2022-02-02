package com.ferelin.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SearchScope

@SearchScope
@Component(dependencies = [SearchDeps::class])
internal interface SearchComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: SearchDeps): Builder
    fun build(): SearchComponent
  }

  fun viewModelFactory(): SearchViewModelFactory
}

interface SearchDeps {
  val searchRequestsUseCase: SearchRequestsUseCase
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
  val dispatchersProvider: DispatchersProvider
}

internal class SearchComponentViewModel(deps: SearchDeps) : ViewModel() {
  val component = DaggerSearchComponent.builder()
    .dependencies(deps)
    .build()
}

internal class SearchComponentViewModelFactory constructor(
  private val searchDeps: SearchDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == SearchComponentViewModel::class.java)
    return SearchComponentViewModel(searchDeps) as T
  }
}