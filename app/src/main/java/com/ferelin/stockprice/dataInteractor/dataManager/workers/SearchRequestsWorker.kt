package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import kotlin.collections.ArrayList

/**
 *  [SearchRequestsWorker] ab ability to:
 *   - Observing [mStateSearchRequests] to display a history of searches.
 *   - Observing [mStatePopularSearchRequests] to display populars search requests.
 *
 *  Also [SearchRequestsWorker] do manually:
 *   - Using [mLocalInteractorHelper] to data caching.
 *   - Using [mSearchRequestsLimit] to control limit of search requests.
 *   - Optimizing search requests size. @see [removeSearchRequestsDuplicates]
 */
class SearchRequestsWorker(private val mLocalInteractorHelper: LocalInteractorHelper) {

    private var mSearchRequests: ArrayList<AdaptiveSearchRequest> = arrayListOf()
    private val mSearchRequestsLimit = 30

    private val mStateSearchRequests =
        MutableStateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mStateSearchRequests

    /*
    * Mocked data
    * */
    private var mPopularSearchRequests: ArrayList<AdaptiveSearchRequest> = arrayListOf(
        AdaptiveSearchRequest("Apple"),
        AdaptiveSearchRequest("Microsoft Corp"),
        AdaptiveSearchRequest("Amazon.com"),
        AdaptiveSearchRequest("Alphabet"),
        AdaptiveSearchRequest("JD.com"),
        AdaptiveSearchRequest("Tesla"),
        AdaptiveSearchRequest("Facebook"),
        AdaptiveSearchRequest("Telefonaktiebolaget"),
        AdaptiveSearchRequest("NVIDIA"),
        AdaptiveSearchRequest("Beigene"),
        AdaptiveSearchRequest("Intel"),
        AdaptiveSearchRequest("Netflix"),
        AdaptiveSearchRequest("Adobe"),
        AdaptiveSearchRequest("Cisco"),
        AdaptiveSearchRequest("Yandex"),
        AdaptiveSearchRequest("Zoom"),
        AdaptiveSearchRequest("Starbucks"),
        AdaptiveSearchRequest("Charter"),
        AdaptiveSearchRequest("Sanofi"),
        AdaptiveSearchRequest("Amgen"),
        AdaptiveSearchRequest("Pepsi")
    )

    private val mStatePopularSearchRequests =
        MutableStateFlow(DataNotificator.DataPrepared(mPopularSearchRequests))
    val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mStatePopularSearchRequests

    fun onDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequests = ArrayList(searches)
        mStateSearchRequests.value = DataNotificator.DataPrepared(mSearchRequests)
    }

    suspend fun cacheNewSearchRequest(searchText: String) {
        val newSearchRequest = AdaptiveSearchRequest(searchText)

        removeSearchRequestsDuplicates(newSearchRequest)
        mSearchRequests.add(0, newSearchRequest)

        if (isSearchRequestsLimitExceeded()) {
            reduceRequestsToLimit()
        }

        mStateSearchRequests.value = DataNotificator.DataUpdated(mSearchRequests)
        mLocalInteractorHelper.cacheSearchRequestsHistory(mSearchRequests)
    }

    /*
    * Example:
    *   Source: [App, Appl, Ap, Facebook, Apple]
    *   Input:  Apple
    *   Output: [Apple, Facebook]
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
        return mSearchRequests.size > mSearchRequestsLimit
    }
}