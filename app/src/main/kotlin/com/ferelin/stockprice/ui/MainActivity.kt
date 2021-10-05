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

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ferelin.feature_chart.view.ChartFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.feature_loading.view.LoadingFragment
import com.ferelin.feature_login.view.LoginFragment
import com.ferelin.feature_news.view.NewsFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies()

        ActivityMainBinding.inflate(layoutInflater).also {
            mViewBinding = it
            setContentView(it.root)
        }

        router.apply {
            bind(this@MainActivity)
            toStartFragment()
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

            this
                .supportFragmentManager
                .addFragmentOnAttachListener { _, fragment ->
                    when (fragment) {
                        is LoadingFragment -> application.appComponent.inject(fragment)
                        is StocksPagerFragment -> application.appComponent.inject(fragment)
                        is IdeasFragment -> application.appComponent.inject(fragment)
                        is NewsFragment -> application.appComponent.inject(fragment)
                        is LoginFragment -> application.appComponent.inject(fragment)
                        is ChartFragment -> application.appComponent.inject(fragment)
                        is AboutPagerFragment -> application.appComponent.inject(fragment)
                        is StocksFragment -> application.appComponent.inject(fragment)
                        is FavouriteFragment -> application.appComponent.inject(fragment)
                    }
                }
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = Color.BLACK
        }
    }
}