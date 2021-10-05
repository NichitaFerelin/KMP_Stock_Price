package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.authentication.di.AuthenticationBindsModule
import com.ferelin.authentication.di.AuthenticationModule
import com.ferelin.core.di.StockStyleModule
import com.ferelin.domain.di.DomainBindsModule
import com.ferelin.domain.di.DomainModule
import com.ferelin.feature_chart.view.ChartFragment
import com.ferelin.feature_forecasts.ForecastsFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.feature_loading.view.LoadingFragment
import com.ferelin.feature_login.view.LoginFragment
import com.ferelin.feature_news.view.NewsFragment
import com.ferelin.feature_section_about.view.AboutPagerFragment
import com.ferelin.feature_section_stocks.view.StocksPagerFragment
import com.ferelin.feature_stocks_default.view.StocksFragment
import com.ferelin.feature_stocks_favourite.view.FavouriteFragment
import com.ferelin.firebase.di.FirebaseBindsModule
import com.ferelin.firebase.di.FirebaseModule
import com.ferelin.local.di.DataLocalModule
import com.ferelin.local.di.DataLocalModuleBinds
import com.ferelin.remote.di.NetworkApiBindsModule
import com.ferelin.remote.di.NetworkApiModule
import com.ferelin.shared.di.ScopeModule
import com.ferelin.stockprice.di.modules.InteractorDependenciesModule
import com.ferelin.stockprice.di.modules.NavigationBindsModule
import com.ferelin.stockprice.di.modules.NetworkModule
import com.ferelin.stockprice.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AuthenticationModule::class,
        AuthenticationBindsModule::class,
        DataLocalModule::class,
        DataLocalModuleBinds::class,
        NetworkApiModule::class,
        NetworkApiBindsModule::class,
        FirebaseModule::class,
        FirebaseBindsModule::class,
        DomainBindsModule::class,
        ScopeModule::class,
        NetworkModule::class,
        InteractorDependenciesModule::class,
        NavigationBindsModule::class,
        StockStyleModule::class,
        DomainModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(fragment: StocksPagerFragment)
    fun inject(fragment: ForecastsFragment)
    fun inject(fragment: IdeasFragment)
    fun inject(fragment: NewsFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: ChartFragment)
    fun inject(fragment: AboutPagerFragment)
    fun inject(fragment: StocksFragment)
    fun inject(fragment: FavouriteFragment)
    fun inject(fragment: LoadingFragment)
}