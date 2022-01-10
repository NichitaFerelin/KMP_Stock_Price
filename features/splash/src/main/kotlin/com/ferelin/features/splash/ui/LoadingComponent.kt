package com.ferelin.features.splash.ui

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LoadingScope

@LoadingScope
@Component(dependencies = [LoadingDeps::class])
internal interface LoadingComponent {
  fun inject(loadingFragment: LoadingFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: LoadingDeps): Builder
    fun build(): LoadingComponent
  }
}

interface LoadingDeps {
  val coordinator: Coordinator
}

interface LoadingDepsProvider {
  var deps: LoadingDeps

  companion object : LoadingDepsProvider by LoadingDepsStore
}

object LoadingDepsStore : LoadingDepsProvider {
  override var deps: LoadingDeps by Delegates.notNull()
}

internal class LoadingComponentViewModel : ViewModel() {
  val loadingComponent = DaggerLoadingComponent.builder()
    .dependencies(LoadingDepsStore.deps)
    .build()
}