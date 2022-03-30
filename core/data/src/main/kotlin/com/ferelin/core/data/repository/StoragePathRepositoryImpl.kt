package com.ferelin.core.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.core.domain.repository.StoragePathRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class StoragePathRepositoryImpl @Inject constructor(
  private val preferencesProvider: PreferencesProvider
) : StoragePathRepository {
  private val pathKey = stringPreferencesKey("storage-path")
  private val authorityKey = stringPreferencesKey("path-authority")

  override val path: Observable<String> = preferencesProvider.dataStore
    .data()
    .map { it[pathKey] ?: "" }
    .distinctUntilChanged()
    .toObservable()

  override val authority: Observable<String> = preferencesProvider.dataStore
    .data()
    .map { it[authorityKey] ?: "" }
    .distinctUntilChanged()
    .toObservable()

  override fun setStoragePath(path: String, authority: String) {
    preferencesProvider.dataStore.updateDataAsync {
      val result = it.toMutablePreferences()
        .apply {
          this[pathKey] = path
          this[authorityKey] = authority
        }
      Single.just(result)
    }
  }
}