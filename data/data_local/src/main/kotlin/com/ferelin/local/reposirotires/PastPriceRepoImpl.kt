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

package com.ferelin.local.reposirotires

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.repositories.PastPriceRepo
import com.ferelin.local.database.PastPriceDao
import com.ferelin.local.mappers.PastPriceMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PastPriceRepoImpl @Inject constructor(
    private val mPastPriceDao: PastPriceDao,
    private val mPastPriceMapper: PastPriceMapper,
    private val mDispatchersProvider: DispatchersProvider
) : PastPriceRepo {

    override suspend fun getAllPastPrices(companyId: Int): List<PastPrice> =
        withContext(mDispatchersProvider.IO) {
            Timber.d("get all past prices by companyId (companyId = $companyId)")
            return@withContext mPastPriceDao
                .getAllPastPrices(companyId)
                .map(mPastPriceMapper::map)
        }

    override suspend fun cacheAllPastPrices(list: List<PastPrice>) =
        withContext(mDispatchersProvider.IO) {
            Timber.d("cache all past prices (size = ${list.size})")
            mPastPriceDao.insertAllPastPrices(
                list = list.map(mPastPriceMapper::map)
            )
        }

    override suspend fun cachePastPrice(pastPrice: PastPrice) =
        withContext(mDispatchersProvider.IO) {
            Timber.d("cache past price(pastPrice = $pastPrice)")
            mPastPriceDao.insertPastPrice(
                pastPriceDBO = mPastPriceMapper.map(pastPrice)
            )
        }
}