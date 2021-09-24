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

package com.ferelin.domain.interactors

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.repositories.PastPriceRepo
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class PastPriceState {
    class Loaded(val pastPrices: List<PastPrice>) : PastPriceState()
    object Error : PastPriceState()
}

@Singleton
class PastPriceInteractor @Inject constructor(
    private val mPastPriceRepo: PastPriceRepo,
    private val mPastPriceSource: PastPriceSource,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    suspend fun getAllPastPrices(companyId: Int): List<PastPrice> {
        return mPastPriceRepo.getAllPastPrices(companyId)
    }

    suspend fun loadPastPrices(companyTicker: String): PastPriceState {
        return mPastPriceSource.loadPastPrices(companyTicker)
            .also { cacheIfLoaded(it) }
    }

    private fun cacheIfLoaded(responseState: PastPriceState) {
        if (responseState is PastPriceState.Loaded) {
            mExternalScope.launch(mCoroutineContextProvider.IO) {
                for (news in responseState.pastPrices) {
                    mPastPriceRepo.cacheAllPastPrices(responseState.pastPrices)
                }
            }
        }
    }
}