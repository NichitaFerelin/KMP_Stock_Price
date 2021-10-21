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

package com.ferelin.di.modules

import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.sources.AuthenticationSource
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.domain.sources.NewsSource
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.domain.syncers.SearchRequestsSyncer
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
class MockedEntitiesModule {

    @Provides
    fun provideCompaniesRemoteRepo(): CompaniesRemoteRepo {
        return Mockito.mock(CompaniesRemoteRepo::class.java)
    }

    @Provides
    fun provideLivePriceSource(): LivePriceSource {
        return Mockito.mock(LivePriceSource::class.java)
    }

    @Provides
    fun provideAuthenticationSource(): AuthenticationSource {
        return Mockito.mock(AuthenticationSource::class.java)
    }

    @Provides
    fun provideCompaniesSyncer() : CompaniesSyncer {
        return Mockito.mock(CompaniesSyncer::class.java)
    }

    @Provides
    fun provideSearchRequestsSyncer() : SearchRequestsSyncer {
        return Mockito.mock(SearchRequestsSyncer::class.java)
    }

    @Provides
    fun provideSearchRequestsRemoteRepo() : SearchRequestsRemoteRepo {
        return Mockito.mock(SearchRequestsRemoteRepo::class.java)
    }

    @Provides
    fun providePastPriceSource() : PastPriceSource {
        return Mockito.mock(PastPriceSource::class.java)
    }

    @Provides
    fun provideNewsSource() : NewsSource {
        return Mockito.mock(NewsSource::class.java)
    }
}