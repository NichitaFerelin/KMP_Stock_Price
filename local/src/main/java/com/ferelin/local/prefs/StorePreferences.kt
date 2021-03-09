package com.ferelin.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StorePreferences(private val mContext: Context) : StorePreferencesHelper {

    private val Context.dataStorePreferences by preferencesDataStore(name = "stockspirce.preferences.db")

    private val mSearchesRequestsHistoryKey = stringSetPreferencesKey("hisotory-key")

    override fun getSearchesHistory(): Flow<Set<String>?> {
        return mContext.dataStorePreferences.data.map {
            it[mSearchesRequestsHistoryKey]
        }
    }

    override suspend fun addSearch(request: String) {
        mContext.dataStorePreferences.edit {
            it[mSearchesRequestsHistoryKey]?.toMutableSet()?.add(request)
            it[mSearchesRequestsHistoryKey]
        }
    }
}