package com.ferelin.features.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotifyPriceRepository {
  val notifyPrice: Flow<Boolean>
  suspend fun setNotifyPrice(notify: Boolean)
}