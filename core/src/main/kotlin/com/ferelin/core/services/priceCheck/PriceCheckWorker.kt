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

package com.ferelin.core.services.priceCheck

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ferelin.core.resolvers.NotificationsResolver
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.useCases.GetFavouriteStocksUseCase
import com.ferelin.domain.useCases.LoadStockPriceUseCase
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PriceCheckWorker constructor(
    context: Context,
    params: WorkerParameters,
    private val getFavouriteStocksUseCase: GetFavouriteStocksUseCase,
    private val loadStockPriceUseCase: LoadStockPriceUseCase,
    private val notificationsResolver: NotificationsResolver
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            updatePricesForFavouriteCompanies()
            Result.success()
        }

    private suspend fun updatePricesForFavouriteCompanies() {
        getFavouriteStocksUseCase
            .getAllFavourites()
            .forEach {
                val priceLoadState =
                    loadStockPriceUseCase.loadStockPrice(it.company.id, it.company.ticker)

                onPriceLoad(it, priceLoadState)
            }
    }

    private fun onPriceLoad(
        hostCompany: CompanyWithStockPrice,
        priceLoadState: LoadState<StockPrice>
    ) {
        priceLoadState.ifPrepared {
            if (it.data != hostCompany.stockPrice) {
                hostCompany.stockPrice = it.data
                notificationsResolver.notifyAboutPriceUpdate(hostCompany)
            }
        }
    }
}