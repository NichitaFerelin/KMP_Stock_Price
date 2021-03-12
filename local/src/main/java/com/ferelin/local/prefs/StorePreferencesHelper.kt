package com.ferelin.local.prefs

import kotlinx.coroutines.flow.Flow

interface StorePreferencesHelper {

    fun getSearchesHistory(): Flow<Set<String>?>

    suspend fun setSearchesHistory(requests: Set<String>)
}