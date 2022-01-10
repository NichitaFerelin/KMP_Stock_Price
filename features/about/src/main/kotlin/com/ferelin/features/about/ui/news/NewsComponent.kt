package com.ferelin.features.about.ui.news

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.NewsUseCase
import com.ferelin.core.network.NetworkListener
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class NewsScope

@NewsScope
@Component(dependencies = [NewsDeps::class])
internal interface NewsComponent {
  fun inject(newsFragment: NewsFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: NewsDeps): Builder
    fun build(): NewsComponent
  }
}

interface NewsDeps {
  val newsUseCase: NewsUseCase
  val networkListener: NetworkListener
}

interface NewsDepsProvider {
  var deps: NewsDeps

  companion object : NewsDepsProvider by NewsDepsStore
}

object NewsDepsStore : NewsDepsProvider {
  override var deps: NewsDeps by Delegates.notNull()
}

internal class NewsComponentViewModel : ViewModel() {
  val newsComponent = DaggerNewsComponent.builder()
    .dependencies(NewsDepsStore.deps)
    .build()
}