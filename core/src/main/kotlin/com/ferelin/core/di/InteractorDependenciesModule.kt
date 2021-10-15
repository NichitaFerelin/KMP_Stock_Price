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

package com.ferelin.core.di

import com.ferelin.domain.interactors.companies.CompaniesInteractorImpl
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractorImpl
import com.ferelin.domain.internals.CompaniesInternal
import com.ferelin.domain.resolvers.LiveTimePriceResolver
import com.ferelin.domain.utils.StockPriceListener
import com.ferelin.shared.AuthenticationListener
import com.ferelin.shared.NetworkListener
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InteractorDependenciesModule {

    @Provides
    fun provideNetworkListeners(
        companiesInteractorImpl: CompaniesInteractorImpl,
        searchRequestsInteractorImpl: SearchRequestsInteractorImpl,
        liveTimePriceResolver: LiveTimePriceResolver
    ): ArrayList<@JvmSuppressWildcards NetworkListener> {
        return arrayListOf(
            companiesInteractorImpl,
            searchRequestsInteractorImpl,
            liveTimePriceResolver
        )
    }
}