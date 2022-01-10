package com.ferelin.features.search.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SearchScope

@SearchScope
@Component(dependencies = [SearchDeps::class])
internal interface SearchComponent {
  fun inject(searchFragment: SearchFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: SearchDeps): Builder
    fun build(): SearchComponent
  }
}

interface SearchDeps {
  val context: Context
  val coordinator: Coordinator
  val searchRequestsUseCase: SearchRequestsUseCase
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val stockPriceUseCase: StockPriceUseCase
  val companyUseCase: CompanyUseCase
}

interface SearchDepsProvider {
  var deps: SearchDeps

  companion object : SearchDepsProvider by SearchDepsStore
}

object SearchDepsStore : SearchDepsProvider {
  override var deps: SearchDeps by Delegates.notNull()
}

internal class SearchComponentViewModel : ViewModel() {
  val searchComponent = DaggerSearchComponent.builder()
    .dependencies(SearchDepsStore.deps)
    .build()
}