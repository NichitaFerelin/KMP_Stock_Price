package com.ferelin.local.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesManagerHelper {

    fun getFavouriteList() : Flow<Set<String>>
    suspend fun setFavouriteList(data: Set<String>)
}