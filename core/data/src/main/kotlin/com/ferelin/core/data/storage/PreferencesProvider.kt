package com.ferelin.core.data.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rxjava3.rxPreferencesDataStore
import androidx.datastore.rxjava3.RxDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PreferencesProvider @Inject constructor(
  context: Context
) {
  private val Context.dataStore: RxDataStore<Preferences> by rxPreferencesDataStore(PREFERENCES_NAME)
  val dataStore: RxDataStore<Preferences> = context.dataStore
}

internal const val PREFERENCES_NAME = "stock.price.preferences"