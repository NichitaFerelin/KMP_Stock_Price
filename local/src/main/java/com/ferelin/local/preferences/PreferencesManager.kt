package com.ferelin.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(context: Context) : PreferencesManagerHelper {

    private val mStoreName = "ferelin.stockprice.preferences.favourites"
    private val mFavouriteListKey = stringSetPreferencesKey("FAVOURITE_LIST")
    private val mDataStorePreferences: DataStore<Preferences> = context.createDataStore(mStoreName)

    override fun getFavouriteList(): Flow<Set<String>> {
        return mDataStorePreferences.data.map {
            it[mFavouriteListKey] ?: emptySet()
        }
    }

    override suspend fun setFavouriteList(data: Set<String>) {
        mDataStorePreferences.edit {
            it[mFavouriteListKey] = data
        }
    }
}