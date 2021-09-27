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

package com.ferelin.stockprice.di.modules

import com.ferelin.domain.interactors.companies.CompaniesInteractorImpl
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractorImpl
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.NetworkListener
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InteractorDependenciesModule {

    @Provides
    @Named("AuthDeps")
    fun provideAuthDependencies(
        companiesInteractorImpl: CompaniesInteractorImpl,
        searchRequestsInteractorImpl: SearchRequestsInteractorImpl
    ): List<AuthenticationListener> {
        return listOf(
            companiesInteractorImpl,
            searchRequestsInteractorImpl
        )
    }

    @Provides
    @Named("NetworkDeps")
    fun provideNetworkDependencies(
        companiesInteractorImpl: CompaniesInteractorImpl,
        searchRequestsInteractorImpl: SearchRequestsInteractorImpl
    ): List<NetworkListener> {
        return listOf(
            companiesInteractorImpl,
            searchRequestsInteractorImpl
        )
    }
}