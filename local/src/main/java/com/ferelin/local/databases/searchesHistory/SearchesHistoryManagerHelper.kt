package com.ferelin.local.databases.searchesHistory

import com.ferelin.local.model.Search
import kotlinx.coroutines.flow.Flow

interface SearchesHistoryManagerHelper {

    fun insertSearch(search: Search)

    fun getSearchesHistory(): Flow<List<Search>>

    fun getPopularSearches(): List<Search>

    fun deleteSearch(name: String)

    fun deleteSearch(search: Search)
}