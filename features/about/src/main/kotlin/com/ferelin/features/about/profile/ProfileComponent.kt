package com.ferelin.features.about.profile

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import com.ferelin.core.ui.params.ProfileParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileScope

@ProfileScope
@Component(dependencies = [ProfileDeps::class])
internal interface ProfileComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun profileParams(profileParams: ProfileParams) : Builder

    fun dependencies(deps: ProfileDeps): Builder
    fun build(): ProfileComponent
  }

  fun viewModelFactory() : ProfileViewModelFactory
}

interface ProfileDeps {
  val profileUseCase: ProfileUseCase
  val companyUseCase: CompanyUseCase
  val dispatchersProvider: DispatchersProvider
}

interface ProfileDepsProvider {
  var deps: ProfileDeps

  companion object : ProfileDepsProvider by ProfileDepsStore
}

object ProfileDepsStore : ProfileDepsProvider {
  override var deps: ProfileDeps by Delegates.notNull()
}