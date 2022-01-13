package com.ferelin.features.about.news

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.NewsUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.NewsParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class NewsScope

@NewsScope
@Component(dependencies = [NewsDeps::class])
internal interface NewsComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun newsParams(newsParams: NewsParams): Builder

    fun dependencies(deps: NewsDeps): Builder
    fun build(): NewsComponent
  }

  fun viewModelFactory() : NewsViewModelFactory
}

interface NewsDeps {
  val newsUseCase: NewsUseCase
  val networkListener: NetworkListener
  val dispatchersProvider: DispatchersProvider
}