package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.stockprice.App
import com.ferelin.stockprice.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
  modules = [

  ]
)
@Singleton
interface AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun context(context: Context): Builder

    fun build() : AppComponent
  }

  fun inject(app: App)
  fun inject(activity: MainActivity)
}