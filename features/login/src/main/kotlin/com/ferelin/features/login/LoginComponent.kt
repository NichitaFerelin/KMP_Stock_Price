package com.ferelin.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.AuthUseCase
import com.ferelin.core.network.NetworkListener
import dagger.Component
import javax.inject.Scope

interface LoginDeps {
  val authUseCase: AuthUseCase
  val networkListener: NetworkListener
  val dispatchersProvider: DispatchersProvider
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class LoginScope

@LoginScope
@Component(dependencies = [LoginDeps::class])
internal interface LoginComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: LoginDeps): Builder
    fun build(): LoginComponent
  }

  fun viewModelFactory(): LoginViewModelFactory
}

internal class LoginComponentViewModel(
  deps: LoginDeps,
) : ViewModel() {
  val component = DaggerLoginComponent.builder()
    .dependencies(deps)
    .build()
}

internal class LoginComponentViewModelFactory(
  private val deps: LoginDeps,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == LoginComponentViewModel::class.java)
    return LoginComponentViewModel(deps) as T
  }
}