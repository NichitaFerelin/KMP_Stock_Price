package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.di.bindsModules.LocalBindsModule
import com.ferelin.stockprice.di.bindsModules.RemoteBindsModule
import com.ferelin.stockprice.di.bindsModules.RepositoryBindsModule
import com.ferelin.stockprice.di.modules.CoroutineModule
import com.ferelin.stockprice.di.modules.DataInteractorModule
import com.ferelin.stockprice.di.modules.LocalDatabasesModule
import com.ferelin.stockprice.di.modules.RemoteModule
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.MainViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoroutineModule::class,
        DataInteractorModule::class,
        LocalDatabasesModule::class,
        RemoteModule::class,
        LocalBindsModule::class,
        RemoteBindsModule::class,
        RepositoryBindsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(viewModel: BaseViewModel)
    fun inject(viewModel: MainViewModel)
}