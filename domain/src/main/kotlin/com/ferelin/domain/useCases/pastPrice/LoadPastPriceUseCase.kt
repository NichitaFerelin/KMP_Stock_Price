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

package com.ferelin.domain.useCases.pastPrice

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.repositories.PastPriceRepo
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * [LoadPastPriceUseCase] allows to interact with stock past prices
 * */
class LoadPastPriceUseCase @Inject constructor(
    private val pastPriceRepo: PastPriceRepo,
    private val pastPriceSource: PastPriceSource,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    /**
     * Load past prices
     * @param relationCompanyId is a company id for which need to load past prices
     * @param relationCompanyTicker is a company ticker for which need to load past prices
     * @return [LoadState] with list of past prices if [LoadState] is successful
     * */
    suspend fun loadAllBy(
        relationCompanyId: Int,
        relationCompanyTicker: String
    ): LoadState<List<PastPrice>> {
        return pastPriceSource
            .loadBy(relationCompanyId, relationCompanyTicker)
            .also { cacheIfPrepared(relationCompanyId, it) }
    }

    private fun cacheIfPrepared(relationCompanyId: Int, loadState: LoadState<List<PastPrice>>) {
        loadState.ifPrepared { preparedState ->
            externalScope.launch {

                // Erase previous
                pastPriceRepo.eraseBy(relationCompanyId)

                pastPriceRepo.insertAll(preparedState.data)
            }
        }
    }
}