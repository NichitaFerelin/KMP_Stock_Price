package com.ferelin.features.authentication.ui

import androidx.lifecycle.ViewModel
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.AuthUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginScope

@LoginScope
@Component(dependencies = [LoginDeps::class])
internal interface LoginComponent {
  fun inject(loginFragment: LoginFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: LoginDeps): Builder
    fun build(): LoginComponent
  }
}

interface LoginDeps {
  val authUseCase: AuthUseCase
  val coordinator: Coordinator
  val networkListener: NetworkListener
  val dispatchersProvider: DispatchersProvider
}

interface LoginDepsProvider {
  var deps: LoginDeps

  companion object : LoginDepsProvider by LoginDepsStore
}

object LoginDepsStore : LoginDepsProvider {
  override var deps: LoginDeps by Delegates.notNull()
}

internal class LoginComponentViewModel : ViewModel() {
  val loginComponent = DaggerLoginComponent.builder()
    .dependencies(LoginDepsStore.deps)
    .build()
}