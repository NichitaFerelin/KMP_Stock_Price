package com.ferelin.local.preferences

import kotlinx.coroutines.flow.Flow

interface StorePreferencesHelper {

    fun getSearchesHistory(): Flow<Set<String>?>

    suspend fun setSearchesHistory(requests: Set<String>)

    fun getFirstTimeLaunchState(): Flow<Boolean?>

    suspend fun setFirstTimeLaunchState(boolean: Boolean)
}