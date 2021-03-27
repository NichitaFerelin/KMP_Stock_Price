package com.ferelin.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class StorePreferences(private val mContext: Context) : StorePreferencesHelper {

    private val Context.dataStorePreferences by preferencesDataStore(name = "stockspirce.preferences.db")

    private val mSearchRequestsHistoryKey = stringSetPreferencesKey("history-key")
    private val mFirstTimeLaunchKey = booleanPreferencesKey("welcome-key")

    override fun getSearchesHistory(): Flow<Set<String>?> {
        return mContext.dataStorePreferences.data.map {
            it[mSearchRequestsHistoryKey]
        }
    }

    override suspend fun setSearchesHistory(requests: Set<String>) {
        mContext.dataStorePreferences.edit {
            it[mSearchRequestsHistoryKey] = requests
        }
    }

    override suspend fun setFirstTimeLaunchState(boolean: Boolean) {
        mContext.dataStorePreferences.edit {
            it[mFirstTimeLaunchKey] = boolean
        }
    }

    override fun getFirstTimeLaunchState(): Flow<Boolean?> {
        return mContext.dataStorePreferences.data.map {
            it[mFirstTimeLaunchKey]
        }
    }
}