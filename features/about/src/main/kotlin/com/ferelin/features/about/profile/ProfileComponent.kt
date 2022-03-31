package com.ferelin.features.about.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import com.ferelin.core.ui.params.ProfileParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface ProfileDeps {
  val profileUseCase: ProfileUseCase
  val companyUseCase: CompanyUseCase
  val dispatchersProvider: DispatchersProvider
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ProfileScope

@ProfileScope
@Component(dependencies = [ProfileDeps::class])
internal interface ProfileComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun params(profileParams: ProfileParams): Builder

    fun dependencies(deps: ProfileDeps): Builder
    fun build(): ProfileComponent
  }

  fun viewModelFactory(): ProfileViewModelFactory
}

internal class ProfileComponentViewModel(
  deps: ProfileDeps,
  params: ProfileParams
) : ViewModel() {
  val component = DaggerProfileComponent.builder()
    .dependencies(deps)
    .params(params)
    .build()
}

internal class ProfileComponentViewModelFactory(
  private val deps: ProfileDeps,
  private val params: ProfileParams
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == ProfileComponentViewModel::class.java)
    return ProfileComponentViewModel(deps, params) as T
  }
}
