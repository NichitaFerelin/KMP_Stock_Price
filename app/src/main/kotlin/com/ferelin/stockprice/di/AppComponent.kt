package com.ferelin.stockprice.di

import android.content.Context
import com.ferelin.authentication.di.AuthenticationBindsModule
import com.ferelin.authentication.di.AuthenticationModule
import com.ferelin.domain.di.DomainBindsModule
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
        NavigationBindsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
}