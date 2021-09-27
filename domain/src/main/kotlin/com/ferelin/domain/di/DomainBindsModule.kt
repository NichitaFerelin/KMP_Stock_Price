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

package com.ferelin.domain.di

import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractorImpl
import com.ferelin.domain.interactors.livePrice.LiveTimePriceInteractor
import com.ferelin.domain.interactors.livePrice.LiveTimePriceInteractorImpl
import com.ferelin.domain.internals.LiveTimePriceInternal
import dagger.Binds
import dagger.Module

@Module
interface DomainBindsModule {

    @Binds
    fun provideCompaniesInternal(
        companiesInteractorImpl: CompaniesInteractorImpl
    ): CompaniesInteractor

    @Binds
    fun provideLiveTimePriceInternal(
        liveTimePriceInteractorImpl: LiveTimePriceInteractorImpl
    ): LiveTimePriceInternal

    @Binds
    fun provideLiveTimePriceInteractor(
        liveTimePriceInteractorImpl: LiveTimePriceInteractorImpl
    ) : LiveTimePriceInteractor
}