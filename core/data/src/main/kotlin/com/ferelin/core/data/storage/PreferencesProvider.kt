package com.ferelin.core.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ferelin.core.ApplicationContext
import javax.inject.Inject

class PreferencesProvider @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)
  val dataStore: DataStore<Preferences> = context.dataStore
}

internal const val PREFERENCES_NAME = "stock.price.preferences"