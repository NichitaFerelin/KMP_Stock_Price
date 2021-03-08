package com.ferelin.local.databases.searchesHistory

import com.ferelin.local.model.Search
import kotlinx.coroutines.flow.Flow

class SearchesHistoryManager(
    searchesHistoryDatabase: SearchesHistoryDatabase
) : SearchesHistoryManagerHelper {

    private val mSearchesDao = searchesHistoryDatabase.searchesDao()

    override fun insertSearch(search: Search) {
        mSearchesDao.insert(search)
    }

    override fun getSearchesHistory(): Flow<List<Search>> {
        return mSearchesDao.getAll()
    }

    override fun getPopularSearches(): List<Search> {
        return SearchesPopular.searches
    }

    override fun deleteSearch(name: String) {
        mSearchesDao.delete(name)
    }

    override fun deleteSearch(search: Search) {
        mSearchesDao.delete(search)
    }
}