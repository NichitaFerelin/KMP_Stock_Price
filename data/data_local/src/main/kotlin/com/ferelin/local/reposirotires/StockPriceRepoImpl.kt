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

import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.local.database.StockPriceDao
import com.ferelin.local.mappers.StockPriceMapper
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StockPriceRepoImpl @Inject constructor(
    private val mStockPriceDao: StockPriceDao,
    private val mStockPriceMapper: StockPriceMapper,
    private val mDispatchersProvider: DispatchersProvider
) : StockPriceRepo {

    override suspend fun getStockPrice(companyId: Int): StockPrice? =
        withContext(mDispatchersProvider.IO) {
            Timber.d("get stock price by company id (companyId = $companyId)")
            val dbo = mStockPriceDao.getStockPrice(companyId)
            return@withContext dbo?.let { mStockPriceMapper.map(it) }
        }

    override suspend fun cacheStockPrice(stockPrice: StockPrice) =
        withContext(mDispatchersProvider.IO) {
            Timber.d("cache stock price (stockPrice = $stockPrice)")
            mStockPriceDao.insertStockPrice(
                dbo = mStockPriceMapper.map(stockPrice)
            )
        }

    override suspend fun updateStockPrice(companyId: Int, price: String, profit: String) =
        withContext(mDispatchersProvider.IO) {
            Timber.d(
                "update stock price (companyId = $companyId, " +
                        "price = $price, profit = $profit)"
            )
            mStockPriceDao.updateStockPrice(companyId, price, profit)
        }
}