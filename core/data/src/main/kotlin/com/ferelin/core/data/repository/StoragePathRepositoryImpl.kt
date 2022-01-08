package com.ferelin.core.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.core.domain.repository.StoragePathRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class StoragePathRepositoryImpl @Inject constructor(
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
    checkBackgroundThread()
    preferencesProvider.dataStore.edit {
      it[pathKey] = path
      it[authorityKey] = authority
    }
  }
}