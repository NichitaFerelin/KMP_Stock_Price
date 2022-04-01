package com.ferelin.core.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.core.domain.repository.NotifyPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class NotifyPriceRepositoryImpl(
  private val preferencesProvider: PreferencesProvider
) : NotifyPriceRepository {
  private val notifyPriceKey = booleanPreferencesKey("notify-price")

  override val notifyPrice: Flow<Boolean> = preferencesProvider.dataStore.data
    .map { it[notifyPriceKey] ?: false }
    .distinctUntilChanged()

  override suspend fun setNotifyPrice(notify: Boolean) {
    preferencesProvider.dataStore.edit {
      it[notifyPriceKey] = notify
    }
  }
}