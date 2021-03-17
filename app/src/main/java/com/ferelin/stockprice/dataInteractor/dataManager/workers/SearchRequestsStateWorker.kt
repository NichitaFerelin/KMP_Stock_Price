package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class SearchRequestsStateWorker(
    private val mLocalInteractorHelper: LocalInteractorHelper
) {
    private var mSearchRequests: MutableList<AdaptiveSearchRequest> = mutableListOf()
    private val mSearchRequestsSaveLimit = 25

    private val mSearchRequestsState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsState

    private val mSearchRequestsUpdateShared = MutableSharedFlow<List<AdaptiveSearchRequest>>()
    val searchRequestsUpdateShared: SharedFlow<List<AdaptiveSearchRequest>>
        get() = mSearchRequestsUpdateShared

    fun onDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequests = searches.toMutableList()
        mSearchRequestsState.value = DataNotificator.DataPrepared(mSearchRequests)
    }

    suspend fun onNewSearch(searchText: String) {
        val newSearchRequest = AdaptiveSearchRequest(searchText)
        for (index in 0 until mSearchRequests.size - 1) {
            val oldSearch = mSearchRequests[index]
            if (newSearchRequest.searchText.contains(oldSearch.searchText)) {
                mSearchRequests.remove(oldSearch)
            }
        }
        mSearchRequests.add(0, newSearchRequest)

        if (mSearchRequests.size >= mSearchRequestsSaveLimit) {
            reduceRequestsToLimit()
        }

        mSearchRequestsState.value = DataNotificator.DataPrepared(mSearchRequests)
        mSearchRequestsUpdateShared.emit(mSearchRequests)
        mLocalInteractorHelper.setSearchesData(mSearchRequests)
    }

    private fun reduceRequestsToLimit() {
        while(mSearchRequests.size >= 30) {
            mSearchRequests.removeLast()
        }
    }
}