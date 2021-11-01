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

import com.ferelin.data_local.database.StockPriceDao
import com.ferelin.data_local.mappers.StockPriceMapper
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StockPriceRepoImpl @Inject constructor(
    private val mStockPriceDao: StockPriceDao,
    private val mStockPriceMapper: StockPriceMapper,
    private val mDispatchersProvider: DispatchersProvider
) : StockPriceRepo {

    override suspend fun insert(stockPrice: StockPrice) =
        withContext(mDispatchersProvider.IO) {
            Timber.d("insert (stockPrice = $stockPrice)")

            val stockPriceDBO = mStockPriceMapper.map(stockPrice)
            mStockPriceDao.insert(stockPriceDBO)
        }

    override suspend fun getBy(relationCompanyId: Int): StockPrice? =
        withContext(mDispatchersProvider.IO) {
            Timber.d("get by (relation company id  = $relationCompanyId)")

            val stockPriceDbo = mStockPriceDao.getBy(relationCompanyId)
            return@withContext stockPriceDbo?.let(mStockPriceMapper::map)
        }

    override suspend fun update(relationCompanyId: Int, price: Double) =
        withContext(mDispatchersProvider.IO) {
            Timber.d(
                "update (relation company id = $relationCompanyId, price = $price)"
            )

            mStockPriceDao.update(relationCompanyId, price)
        }
}