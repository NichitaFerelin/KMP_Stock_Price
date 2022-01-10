package com.ferelin.stockprice

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.view.routing.RouterHost
import com.ferelin.features.about.ui.about.AboutDepsStore
import com.ferelin.features.about.ui.about.AboutScreenKey
import com.ferelin.features.about.ui.chart.ChartDepsStore
import com.ferelin.features.about.ui.chart.ChartScreenKey
import com.ferelin.features.about.ui.news.NewsDepsStore
import com.ferelin.features.about.ui.news.NewsScreenKey
import com.ferelin.features.about.ui.profile.ProfileDepsStore
import com.ferelin.features.about.ui.profile.ProfileScreenKey
import com.ferelin.features.authentication.ui.LoginDepsStore
import com.ferelin.features.authentication.ui.LoginScreenKey
import com.ferelin.features.search.ui.SearchDepsStore
import com.ferelin.features.search.ui.SearchScreenKey
import com.ferelin.features.settings.ui.SettingsDepsStore
import com.ferelin.features.settings.ui.SettingsScreenKey
import com.ferelin.features.splash.ui.LoadingDepsStore
import com.ferelin.features.splash.ui.LoadingScreenKey
import com.ferelin.features.stocks.ui.common.CommonDepsStore
import com.ferelin.features.stocks.ui.common.CommonScreenKey
import com.ferelin.features.stocks.ui.defaults.StocksDepsStore
import com.ferelin.features.stocks.ui.defaults.StocksScreenKey
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksDepsStore
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksScreenKey
import com.ferelin.stockprice.databinding.ActivityMainBinding
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity : AppCompatActivity() {
  private var viewBinding: ActivityMainBinding? = null

  @Inject
  lateinit var routerHost: RouterHost

  @Inject
  lateinit var coordinator: Coordinator

  private val fragmentLifecycleCallbacks by lazy(NONE) {
    object : FragmentManager.FragmentLifecycleCallbacks() {

      override fun onFragmentPreAttached(
        fm: FragmentManager,
        fragment: Fragment,
        context: Context
      ) {
        super.onFragmentPreAttached(fm, fragment, context)

        application.let { app ->
          if (app !is App) {
            return
          }

          when (fragment::class.java.simpleName) {
            SearchScreenKey.controllerConfig.key -> SearchDepsStore.deps = app.appComponent
            AboutScreenKey.controllerConfig.key -> AboutDepsStore.deps = app.appComponent
            ChartScreenKey.controllerConfig.key -> ChartDepsStore.deps = app.appComponent
            NewsScreenKey.controllerConfig.key -> NewsDepsStore.deps = app.appComponent
            ProfileScreenKey.controllerConfig.key -> ProfileDepsStore.deps = app.appComponent
            LoginScreenKey.controllerConfig.key -> LoginDepsStore.deps = app.appComponent
            SettingsScreenKey.controllerConfig.key -> SettingsDepsStore.deps = app.appComponent
            LoadingScreenKey.controllerConfig.key -> LoadingDepsStore.deps = app.appComponent
            CommonScreenKey.controllerConfig.key -> CommonDepsStore.deps = app.appComponent
            StocksScreenKey.controllerConfig.key -> StocksDepsStore.deps = app.appComponent
            FavouriteStocksScreenKey.controllerConfig.key -> FavouriteStocksDepsStore.deps = app.appComponent
          }
        }
      }

      override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent()
    injectDeps()

    supportFragmentManager.registerFragmentLifecycleCallbacks(
      fragmentLifecycleCallbacks,
      true
    )

    routerHost.host = this
    if (savedInstanceState == null) {
      coordinator.initialRoute()
    }
  }

  override fun onDestroy() {
    viewBinding = null
    routerHost.host = null
    super.onDestroy()
  }

  private fun setContent() {
    ActivityMainBinding.inflate(layoutInflater).also {
      viewBinding = it
      setContentView(it.root)
    }
  }

  private fun injectDeps() {
    application.let { app ->
      if (app is App) {
        app.appComponent.inject(this)
      }
    }
  }
}