package com.ferelin.features.about.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    fun params(newsParams: NewsParams): Builder

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

internal class NewsComponentViewModel(
  deps: NewsDeps,
  params: NewsParams
) : ViewModel() {
  val component = DaggerNewsComponent.builder()
    .dependencies(deps)
    .params(params)
    .build()
}

internal class NewsComponentViewModelFactory(
  private val deps: NewsDeps,
  private val params: NewsParams
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == NewsComponentViewModel::class.java)
    return NewsComponentViewModel(deps, params) as T
  }
}