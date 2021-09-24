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

package com.ferelin.remote.sources

import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.remote.mappers.LivePriceMapper
import com.ferelin.remote.resolvers.LivePriceSocketResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LivePriceSourceImpl(
    private val mLivePriceSocketResolver: LivePriceSocketResolver,
    private val mLivePriceMapper: LivePriceMapper,
) : LivePriceSource {

    override fun observeLiveTimeUpdates(): Flow<LiveTimePrice?> {
        return mLivePriceSocketResolver
            .openConnection()
            .map(mLivePriceMapper::map)
    }

    override suspend fun subscribeCompanyOnUpdates(companyTicker: String) {
        mLivePriceSocketResolver.subscribe(companyTicker)
    }

    override suspend fun unsubscribeCompanyFromUpdates(companyTicker: String) {
        mLivePriceSocketResolver.unsubscribe(companyTicker)
    }
}