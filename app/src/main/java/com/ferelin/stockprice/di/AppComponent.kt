package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.MainViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        HelpersBindsModule::class,
        DataInteractorModule::class,
        CoroutineModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(viewModel: BaseViewModel)
    fun inject(viewModel: MainViewModel)

    fun inject(navigator: Navigator)
}