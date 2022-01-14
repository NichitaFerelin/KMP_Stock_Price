package com.ferelin.features.about

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