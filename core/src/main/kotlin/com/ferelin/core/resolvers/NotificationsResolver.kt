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

package com.ferelin.core.resolvers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ferelin.core.R
import com.ferelin.core.utils.buildProfitString
import com.ferelin.domain.entities.CompanyWithStockPrice
import javax.inject.Inject

class NotificationsResolver @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val updateNotificationsChannelId = "1"
        private const val updateNotificationsGroup = "update-group"
    }

    val downloadTitle = context.getString(R.string.titleDownload)
    val downloadDescription = context.getString(R.string.descriptionDownload)

    var isFirstNotification = true

    fun notifyAboutPriceUpdate(companyWithStockPrice: CompanyWithStockPrice) {
        createNotificationChannel()

        val title =
            context.getString(R.string.app_name) + " - " + companyWithStockPrice.company.ticker
        val content = buildProfitString(
            companyWithStockPrice.stockPrice?.currentPrice ?: 0.0,
            companyWithStockPrice.stockPrice?.previousClosePrice ?: 0.0
        )

        val builder = NotificationCompat.Builder(context, updateNotificationsChannelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(updateNotificationsGroup)

        builder.priority = if (isFirstNotification) {
            isFirstNotification = false
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_LOW
        }

        NotificationManagerCompat
            .from(context)
            .notify(companyWithStockPrice.company.id, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.titleNotificationChannelUpdates)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(updateNotificationsChannelId, name, importance)
            channel.description = context.getString(R.string.descriptionNotificationChannelUpdates)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}