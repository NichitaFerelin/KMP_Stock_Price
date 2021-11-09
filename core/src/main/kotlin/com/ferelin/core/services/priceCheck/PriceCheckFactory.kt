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
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ferelin.core.resolvers.NotificationsResolver
import com.ferelin.domain.useCases.GetFavouriteStocksUseCase
import com.ferelin.domain.useCases.LoadStockPriceUseCase
import javax.inject.Inject

class PriceCheckFactory @Inject constructor(
    private val getFavouriteStocksUseCase: GetFavouriteStocksUseCase,
    private val loadStockPriceUseCase: LoadStockPriceUseCase,
    private val notificationsResolver: NotificationsResolver
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            PriceCheckWorker::class.java.name -> PriceCheckWorker(
                appContext,
                workerParameters,
                getFavouriteStocksUseCase,
                loadStockPriceUseCase,
                notificationsResolver
            )
            else -> null
        }
    }
}