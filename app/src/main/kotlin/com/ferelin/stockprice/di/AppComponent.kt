package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.core.data.di.NetworkModule
import com.ferelin.core.data.di.RepositoryModule
import com.ferelin.core.data.di.StorageModule
import com.ferelin.core.di.CoroutineModule
import com.ferelin.core.di.NetworkListenerModule
import com.ferelin.core.domain.di.UseCaseModule
import com.ferelin.core.ui.di.RouterModule
import com.ferelin.navigation.CoordinatorModule
import com.ferelin.stockprice.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    NetworkModule::class,
    RepositoryModule::class,
    StorageModule::class,
    UseCaseModule::class,
    RouterModule::class,
    CoroutineModule::class,
    NetworkListenerModule::class,
    CoordinatorModule::class
  ]
)
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun context(context: Context): Builder
    fun build(): AppComponent
  }

  fun inject(mainActivity: MainActivity)
}