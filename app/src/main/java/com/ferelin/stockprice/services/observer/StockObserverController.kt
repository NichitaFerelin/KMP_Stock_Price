package com.ferelin.stockprice.services.observer

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

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany

/**
 * [StockObserverController] provides easy way to control [StockObserverService]
 * */
object StockObserverController {

    fun updateService(context: Context, company: AdaptiveCompany) {
        val serviceIntent = Intent(context, StockObserverService::class.java).apply {
            putExtra(StockObserverService.KEY_COMPANY_NAME_STR, company.companyProfile.name)
            putExtra(StockObserverService.KEY_PRICE_STR, company.companyDayData.currentPrice)
            putExtra(StockObserverService.KEY_PROFIT_STR, company.companyDayData.profit)
            putExtra(
                StockObserverService.KEY_PROFIT_TEXT_COLOR_RES,
                company.companyStyle.dayProfitBackground
            )
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun stopService(context: Context) {
        val serviceIntent = Intent(context, StockObserverService::class.java).apply {
            putExtra(StockObserverService.KEY_STOP, StockObserverService.KEY_STOP)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}