package com.ferelin.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotifyPriceRepository {
  val notifyPrice: Flow<Boolean>
  suspend fun setNotifyPrice(notify: Boolean)
}