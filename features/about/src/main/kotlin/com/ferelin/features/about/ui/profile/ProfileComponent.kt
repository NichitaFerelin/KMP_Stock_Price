package com.ferelin.features.about.ui.profile

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ProfileScope

@ProfileScope
@Component(dependencies = [ProfileDeps::class])
internal interface ProfileComponent {
  fun inject(profileFragment: ProfileFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: ProfileDeps): Builder
    fun build(): ProfileComponent
  }
}

interface ProfileDeps {
  val profileUseCase: ProfileUseCase
  val companyUseCase: CompanyUseCase
}

interface ProfileDepsProvider {
  var deps: ProfileDeps

  companion object : ProfileDepsProvider by ProfileDepsStore
}

object ProfileDepsStore : ProfileDepsProvider {
  override var deps: ProfileDeps by Delegates.notNull()
}

internal class ProfileComponentViewModel : ViewModel() {
  val profileComponent = DaggerProfileComponent.builder()
    .dependencies(ProfileDepsStore.deps)
    .build()
}