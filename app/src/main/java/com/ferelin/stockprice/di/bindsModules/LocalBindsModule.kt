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

package com.ferelin.stockprice.di.bindsModules

import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerImpl
import com.ferelin.local.json.JsonManager
import com.ferelin.local.json.JsonManagerImpl
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesImpl
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.dataInteractor.DataInteractorImpl
import dagger.Binds
import dagger.Module

/**
 * [LocalBindsModule] contains providers for local entities
 * */
@Module
abstract class LocalBindsModule {

    @Binds
    abstract fun provideDataInteractor(dataInteractor: DataInteractorImpl): DataInteractor

    @Binds
    abstract fun provideLocalManager(local: LocalManagerImpl): LocalManager

    @Binds
    abstract fun provideJsonManager(json: JsonManagerImpl): JsonManager

    @Binds
    abstract fun provideStorePreferences(store: StorePreferencesImpl): StorePreferences
}