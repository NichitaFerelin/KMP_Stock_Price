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
import com.ferelin.feature_chart.view.ChartFragment
import com.ferelin.feature_forecasts.ForecastsFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.feature_loading.view.LoadingFragment
import com.ferelin.feature_login.view.LoginFragment
import com.ferelin.feature_news.view.NewsFragment
import com.ferelin.feature_profile.view.ProfileFragment
import com.ferelin.feature_search.view.SearchFragment
import com.ferelin.feature_section_about.view.AboutPagerFragment
import com.ferelin.feature_section_stocks.view.StocksPagerFragment
import com.ferelin.feature_stocks_default.view.StocksFragment
import com.ferelin.feature_stocks_favourite.view.FavouriteFragment
import com.ferelin.navigation.Router
import com.ferelin.stockprice.App
import com.ferelin.stockprice.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null

    @Inject
    lateinit var router: Router

    private val mFragmentLifecycleCallbacks by lazy(LazyThreadSafetyMode.NONE) {
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
                        is StocksPagerFragment -> app.appComponent.inject(f)
                        is StocksFragment -> app.appComponent.inject(f)
                        is FavouriteFragment -> app.appComponent.inject(f)
                        is AboutPagerFragment -> app.appComponent.inject(f)
                        is ProfileFragment -> app.appComponent.inject(f)
                        is ChartFragment -> app.appComponent.inject(f)
                        is NewsFragment -> app.appComponent.inject(f)
                        is ForecastsFragment -> app.appComponent.inject(f)
                        is IdeasFragment -> app.appComponent.inject(f)
                        is LoginFragment -> app.appComponent.inject(f)
                        is SearchFragment -> app.appComponent.inject(f)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            mFragmentLifecycleCallbacks,
            true
        )

        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also {
            mViewBinding = it
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
        super.onDestroy()
        mViewBinding = null
        router.unbind()
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