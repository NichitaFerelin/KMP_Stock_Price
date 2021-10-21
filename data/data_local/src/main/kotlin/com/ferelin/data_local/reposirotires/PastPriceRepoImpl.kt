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

package com.ferelin.data_local.reposirotires

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.repositories.PastPriceRepo
import com.ferelin.data_local.database.PastPriceDao
import com.ferelin.data_local.mappers.PastPriceMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PastPriceRepoImpl @Inject constructor(
    private val pastPriceDao: PastPriceDao,
    private val pastPriceMapper: PastPriceMapper,
    private val dispatchersProvider: DispatchersProvider
) : PastPriceRepo {

    override suspend fun insertAll(pastPrices: List<PastPrice>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (past prices size = ${pastPrices.size})")

            pastPriceDao.insertAll(
                pastPrices.map(pastPriceMapper::map)
            )
        }

    override suspend fun insert(pastPrice: PastPrice) =
        withContext(dispatchersProvider.IO) {
            Timber.d("cache past price(pastPrice = $pastPrice)")
            pastPriceDao.insert(
                pastPriceDBO = pastPriceMapper.map(pastPrice)
            )
        }

    override suspend fun getAllBy(relationCompanyId: Int): List<PastPrice> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all by (relation company id = $relationCompanyId)")

            return@withContext pastPriceDao
                .getAll(relationCompanyId)
                .map(pastPriceMapper::map)
        }

    override suspend fun eraseBy(relationCompanyId: Int) =
        withContext(dispatchersProvider.IO) {
            Timber.d("erase by (relation company id = $relationCompanyId)")

            pastPriceDao.eraseBy(relationCompanyId)
        }
}