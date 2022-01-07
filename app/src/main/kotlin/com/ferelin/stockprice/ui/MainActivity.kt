/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.stockprice.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ferelin.core.network.NetworkListener
import com.ferelin.features.about.ui.chart.ChartFragment
import com.ferelin.feature_forecasts.ForecastsFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.features.splash.ui.LoadingFragment
import com.ferelin.features.authentication.ui.LoginFragment
import com.ferelin.features.about.ui.news.NewsFragment
import com.ferelin.features.about.ui.profile.ProfileFragment
import com.ferelin.features.search.ui.SearchFragment
import com.ferelin.features.about.ui.about.AboutFragment
import com.ferelin.features.stocks.ui.main.MainFragment
import com.ferelin.features.settings.ui.SettingsFragment
import com.ferelin.features.stocks.ui.defaults.StocksFragment
import com.ferelin.features.stocks.ui.favourites.FavouriteStocksFragment
import com.ferelin.navigation.Router
import com.ferelin.stockprice.App
import com.ferelin.stockprice.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var viewBinding: ActivityMainBinding? = null

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var networkListener: NetworkListener

    private val fragmentLifecycleCallbacks by lazy(LazyThreadSafetyMode.NONE) {
        object : FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentPreAttached(
                fm: FragmentManager,
                f: Fragment,
                context: Context
            ) {
                super.onFragmentPreAttached(fm, f, context)

                application.let { app ->
                    if (app !is App) {
                        return
                    }

                    when (f) {
                        is LoadingFragment -> app.appComponent.inject(f)
                        is MainFragment -> app.appComponent.inject(f)
                        is StocksFragment -> app.appComponent.inject(f)
                        is FavouriteStocksFragment -> app.appComponent.inject(f)
                        is AboutFragment -> app.appComponent.inject(f)
                        is ProfileFragment -> app.appComponent.inject(f)
                        is ChartFragment -> app.appComponent.inject(f)
                        is NewsFragment -> app.appComponent.inject(f)
                        is ForecastsFragment -> app.appComponent.inject(f)
                        is IdeasFragment -> app.appComponent.inject(f)
                        is LoginFragment -> app.appComponent.inject(f)
                        is SearchFragment -> app.appComponent.inject(f)
                        is SettingsFragment -> app.appComponent.inject(f)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycleCallbacks,
            true
        )

        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).also {
            viewBinding = it
            setContentView(it.root)
        }

        router.apply {
            bind(this@MainActivity)

            if (savedInstanceState == null) {
                toStartFragment()
            }
        }

        setStatusBarColor()
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        router.unbind()
        viewBinding = null
        super.onDestroy()
    }

    private fun injectDependencies() {
        val application = application
        if (application is App) {
            application.appComponent.inject(this)
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}