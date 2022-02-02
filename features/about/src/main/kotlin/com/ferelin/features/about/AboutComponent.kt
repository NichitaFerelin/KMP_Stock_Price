package com.ferelin.features.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.features.about.chart.ChartDeps
import com.ferelin.features.about.news.NewsDeps
import com.ferelin.features.about.profile.ProfileDeps
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class AboutScope

@AboutScope
@Component(dependencies = [AboutDeps::class])
internal interface AboutComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun params(aboutParams: AboutParams): Builder

    fun dependencies(deps: AboutDeps): Builder
    fun build(): AboutComponent
  }

  fun viewModelFactory(): AboutViewModelFactory
}

interface AboutDeps : ChartDeps, NewsDeps, ProfileDeps {
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
}


internal class AboutComponentViewModel(
  deps: AboutDeps,
  params: AboutParams
) : ViewModel() {
  val component = DaggerAboutComponent.builder()
    .dependencies(deps)
    .params(params)
    .build()
}

internal class AboutComponentViewModelFactory(
  private val deps: AboutDeps,
  private val params: AboutParams
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == AboutComponentViewModel::class.java)
    return AboutComponentViewModel(deps, params) as T
  }
}