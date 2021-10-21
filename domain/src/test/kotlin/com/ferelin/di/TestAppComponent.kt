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

package com.ferelin.di

import android.content.Context
import com.ferelin.di.modules.DataLocalTestModule
import com.ferelin.di.modules.MockedEntitiesModule
import com.ferelin.di.modules.ScopeTestModule
import com.ferelin.domain.di.DomainBindsModule
import com.ferelin.domain.di.DomainModule
import com.ferelin.interactorTests.*
import com.ferelin.data_local.di.DataLocalModuleBinds
import com.ferelin.repoTests.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MockedEntitiesModule::class,
        DomainBindsModule::class,
        DomainModule::class,
        DataLocalTestModule::class,
        DataLocalModuleBinds::class,
        ScopeTestModule::class
    ]
)
interface TestAppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestAppComponent
    }

    fun inject(companiesInteractorTest: CompaniesInteractorTest)
    fun inject(firstLaunchInteractorTest: FirstLaunchInteractorTest)
    fun inject(newsInteractorTest: NewsInteractorTest)
    fun inject(pastPriceInteractorTest: PastPriceInteractorTest)
    fun inject(profileInteractorTest: ProfileInteractorTest)
    fun inject(searchRequestsInteractorTest: SearchRequestsInteractorTest)

    fun inject(companiesLocalRepoTest: CompaniesLocalRepoTest)
    fun inject(firstLaunchRepoTest: FirstLaunchRepoTest)
    fun inject(newsRepoTest: NewsRepoTest)
    fun inject(pastPriceRepoTest: PastPriceRepoTest)
    fun inject(profileRepoTest: ProfileRepoTest)
    fun inject(searchRequestsLocalRepoTest: SearchRequestsLocalRepoTest)
    fun inject(stockPriceRepoTest: StockPriceRepoTest)
}