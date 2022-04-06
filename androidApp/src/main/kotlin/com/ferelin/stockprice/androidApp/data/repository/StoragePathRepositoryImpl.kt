package com.ferelin.stockprice.androidApp.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ferelin.stockprice.androidApp.data.storage.PreferencesProvider
import com.ferelin.stockprice.androidApp.domain.repository.StoragePathRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class StoragePathRepositoryImpl(
  private val preferencesProvider: PreferencesProvider
) : StoragePathRepository {
  private val pathKey = stringPreferencesKey("storage-path")
  private val authorityKey = stringPreferencesKey("path-authority")

  override val path: Flow<String> = preferencesProvider.dataStore.data
    .map { it[pathKey] ?: "" }
    .distinctUntilChanged()

  override val authority: Flow<String> = preferencesProvider.dataStore.data
    .map { it[authorityKey] ?: "" }
    .distinctUntilChanged()

  override suspend fun setStoragePath(path: String, authority: String) {
    preferencesProvider.dataStore.edit {
      it[pathKey] = path
      it[authorityKey] = authority
    }
  }
}