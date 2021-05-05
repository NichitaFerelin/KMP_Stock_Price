package com.ferelin.stockprice.services.observer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany

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