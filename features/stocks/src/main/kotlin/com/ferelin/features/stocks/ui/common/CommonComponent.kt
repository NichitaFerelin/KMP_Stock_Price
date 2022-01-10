package com.ferelin.features.stocks.ui.common

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CommonScope

@CommonScope
@Component(dependencies = [CommonDeps::class])
internal interface CommonComponent {
  fun inject(commonFragment: CommonFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: CommonDeps): Builder
    fun build(): CommonComponent
  }
}

interface CommonDeps {
  val cryptoPriceUseCase: CryptoPriceUseCase
  val coordinator: Coordinator
  val cryptoUseCase: CryptoUseCase
  val networkListener: NetworkListener
}

interface CommonDepsProvider {
  var deps: CommonDeps

  companion object : CommonDepsProvider by CommonDepsStore
}

object CommonDepsStore : CommonDepsProvider {
  override var deps: CommonDeps by Delegates.notNull()
}

internal class CommonComponentViewModel : ViewModel() {
  val commonComponent = DaggerCommonComponent.builder()
    .dependencies(CommonDepsStore.deps)
    .build()
}