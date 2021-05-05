package com.ferelin.stockprice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.ferelin.stockprice.dataInteractor.DataInteractor


class App : Application() {

    val dataInteractor: DataInteractor by lazy {
        DataInteractor.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                PRICE_OBSERVER_CHANNEL_ID,
                this.resources.getString(R.string.titleChannelStockPrice),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val PRICE_OBSERVER_CHANNEL_ID = "1001"
    }
}