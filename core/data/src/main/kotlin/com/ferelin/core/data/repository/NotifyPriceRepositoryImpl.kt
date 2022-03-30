package com.ferelin.core.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.core.domain.repository.NotifyPriceRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class NotifyPriceRepositoryImpl @Inject constructor(
  private val preferencesProvider: PreferencesProvider
) : NotifyPriceRepository {
  private val notifyPriceKey = booleanPreferencesKey("notify-price")

  override val notifyPrice: Observable<Boolean> = preferencesProvider.dataStore
    .data()
    .map { it[notifyPriceKey] ?: false }
    .distinctUntilChanged()
    .toObservable()

  override fun setNotifyPrice(notify: Boolean) {
    preferencesProvider.dataStore.updateDataAsync {
      val result = it.toMutablePreferences().apply {
        this[notifyPriceKey] = notify
      }
      Single.just(result)
    }
  }
}