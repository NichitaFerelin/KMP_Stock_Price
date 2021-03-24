package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchRequestsStateWorker(private val mLocalInteractorHelper: LocalInteractorHelper) {

    private var mSearchRequests: MutableList<AdaptiveSearchRequest> = mutableListOf()
    private val mSearchRequestsSaveLimit = 25

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
        optimizeSearchRequests(newSearchRequest)
        mSearchRequests.add(0, newSearchRequest)

        if (mSearchRequests.size >= mSearchRequestsSaveLimit) {
            reduceRequestsToLimit()
        }

        mSearchRequestsState.value = DataNotificator.DataPrepared(mSearchRequests)
        mLocalInteractorHelper.setSearchRequestsHistory(mSearchRequests)
    }

    private fun optimizeSearchRequests(newSearchRequest: AdaptiveSearchRequest) {
        var endBorder = mSearchRequests.size
        var cursor = 0
        while (cursor < endBorder) {
            val oldSearch = mSearchRequests[cursor]
            if (newSearchRequest.searchText.contains(oldSearch.searchText)) {
                mSearchRequests.remove(oldSearch)
                endBorder--
            }
            cursor++
        }
    }

    private fun reduceRequestsToLimit() {
        while (mSearchRequests.size >= 30) {
            mSearchRequests.removeLast()
        }
    }
}