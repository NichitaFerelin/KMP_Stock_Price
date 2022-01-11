package com.ferelin.features.about.ui.about

import androidx.lifecycle.ViewModel
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AboutScope

@AboutScope
@Component(dependencies = [AboutDeps::class])
internal interface AboutComponent {
  fun inject(aboutFragment: AboutFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: AboutDeps): Builder
    fun build(): AboutComponent
  }
}

interface AboutDeps {
  val coordinator: Coordinator
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val dispatchersProvider: DispatchersProvider
}

interface AboutDepsProvider {
  var deps: AboutDeps

  companion object : AboutDepsProvider by AboutDepsStore
}

object AboutDepsStore : AboutDepsProvider {
  override var deps: AboutDeps by Delegates.notNull()
}

internal class AboutComponentViewModel : ViewModel() {
  val aboutComponent = DaggerAboutComponent.builder()
    .dependencies(AboutDepsStore.deps)
    .build()
}