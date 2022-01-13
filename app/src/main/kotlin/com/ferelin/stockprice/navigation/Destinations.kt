package com.ferelin.stockprice.navigation

import com.ferelin.core.ui.params.AboutParams
import com.ferelin.features.about.about.AboutComponent
import com.ferelin.features.about.about.DaggerAboutComponent
import com.ferelin.features.authentication.DaggerLoginComponent
import com.ferelin.features.authentication.LoginComponent
import com.ferelin.features.search.DaggerSearchComponent
import com.ferelin.features.search.SearchComponent
import com.ferelin.features.settings.DaggerSettingsComponent
import com.ferelin.features.settings.SettingsComponent
import com.ferelin.features.stocks.common.CommonComponent
import com.ferelin.features.stocks.common.DaggerCommonComponent
import com.ferelin.stockprice.di.AppComponent

internal sealed class Destination(val key: String) {
  object SplashDestination : Destination("splash")

  object AboutDestination : Destination("about") {
    const val ARG_ID = "id"
    const val ARG_NAME = "name"
    const val ARG_TICKER = "ticker"

    fun component(
      appComponent: AppComponent,
      aboutParams: AboutParams
    ): AboutComponent {
      return DaggerAboutComponent.builder()
        .params(aboutParams)
        .dependencies(appComponent)
        .build()
    }
  }

  object AuthenticationDestination : Destination("authentication") {
    fun component(appComponent: AppComponent): LoginComponent {
      return DaggerLoginComponent.builder()
        .dependencies(appComponent)
        .build()
    }
  }

  object SearchDestination : Destination("search") {
    fun component(appComponent: AppComponent): SearchComponent {
      return DaggerSearchComponent.builder()
        .dependencies(appComponent)
        .build()
    }
  }

  object SettingsDestination : Destination("settings") {
    fun component(appComponent: AppComponent): SettingsComponent {
      return DaggerSettingsComponent.builder()
        .dependencies(appComponent)
        .build()
    }
  }

  object StocksDestination : Destination("stocks") {
    fun component(appComponent: AppComponent): CommonComponent {
      return DaggerCommonComponent.builder()
        .dependencies(appComponent)
        .build()
    }
  }
}