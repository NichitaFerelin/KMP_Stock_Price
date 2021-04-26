package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

/*
* Worker that is responsible for:
*   - Providing search requests history
*   - Caching search requests
*   - Optimizing search requests size
* */
class SearchRequestsStateWorker(private val mLocalInteractorHelper: LocalInteractorHelper) {

    private var mSearchRequests: MutableList<AdaptiveSearchRequest> = mutableListOf()
    private val mSearchRequestsSaveLimit = 30

    private val mSearchRequestsState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsState

    fun onDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequests = searches.toMutableList()
        mSearchRequestsState.value = DataNotificator.DataPrepared(mSearchRequests)
    }

    suspend fun onNewSearch(searchText: String) {
        val newSearchRequest = AdaptiveSearchRequest(searchText)
        removeSearchRequestsDuplicates(newSearchRequest)
        mSearchRequests.add(0, newSearchRequest)

        if (isSearchRequestsLimitExceeded()) {
            reduceRequestsToLimit()
        }
        mSearchRequestsState.value = DataNotificator.DataUpdated(mSearchRequests)
        mLocalInteractorHelper.updateSearchRequestsHistory(mSearchRequests)
    }

    /*
    * If new search-text-request is contains in history -> item in history will be removed.
    * */
    private fun removeSearchRequestsDuplicates(newSearchRequest: AdaptiveSearchRequest) {
        var endBorder = mSearchRequests.size
        var listCursor = 0
        val newSearchRequestStr = newSearchRequest.searchText.toLowerCase(Locale.ROOT)
        while (listCursor < endBorder) {
            val searchRequestStr = mSearchRequests[listCursor].searchText.toLowerCase(Locale.ROOT)
            if (newSearchRequestStr.contains(searchRequestStr)) {
                mSearchRequests.removeAt(listCursor)
                endBorder--
            }
            listCursor++
        }
    }

    private fun reduceRequestsToLimit() {
        mSearchRequests.removeLast()
    }

    private fun isSearchRequestsLimitExceeded(): Boolean {
        return mSearchRequests.size > mSearchRequestsSaveLimit
    }
}