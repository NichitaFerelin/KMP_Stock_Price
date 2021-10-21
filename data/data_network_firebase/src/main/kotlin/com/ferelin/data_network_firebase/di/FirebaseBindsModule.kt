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

package com.ferelin.data_network_firebase.di

import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.domain.syncers.CompaniesSyncer
import com.ferelin.domain.syncers.SearchRequestsSyncer
import com.ferelin.data_network_firebase.repositories.CompaniesRemoteRepoImpl
import com.ferelin.data_network_firebase.repositories.SearchRequestsRemoteRepoImpl
import com.ferelin.data_network_firebase.syncers.CompaniesSyncerImpl
import com.ferelin.data_network_firebase.syncers.SearchRequestsSyncerImpl
import dagger.Binds
import dagger.Module

@Module
interface FirebaseBindsModule {

    @Binds
    fun provideCompaniesRemoteRepo(
        companiesRemoteRepoImpl: CompaniesRemoteRepoImpl
    ): CompaniesRemoteRepo

    @Binds
    fun provideSearchRequestsRemoteRepo(
        searchRequestsRemoteRepoImpl: SearchRequestsRemoteRepoImpl
    ): SearchRequestsRemoteRepo

    @Binds
    fun provideCompaniesSyncer(companiesSyncerImpl: CompaniesSyncerImpl): CompaniesSyncer

    @Binds
    fun provideSearchRequestsSyncer(
        searchRequestsSyncerImpl: SearchRequestsSyncerImpl
    ): SearchRequestsSyncer
}