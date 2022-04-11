package com.ferelin.stockprice.shared.domain.usecase

import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.entity.SearchRequest
import com.ferelin.stockprice.shared.domain.repository.SearchRequestsRepository
import kotlinx.coroutines.flow.*

interface SearchRequestsUseCase {
    val searchRequests: Flow<List<SearchRequest>>
    val searchRequestsLce: Flow<LceState>
    val popularSearchRequests: Flow<List<SearchRequest>>
    val popularSearchRequestsLce: Flow<LceState>
    suspend fun onNewSearchRequest(request: String, resultsSize: Int)
    suspend fun eraseAll()
}

internal class SearchRequestsUseCaseImpl(
    private val searchRequestsRepository: SearchRequestsRepository
) : SearchRequestsUseCase {
    override val searchRequests: Flow<List<SearchRequest>> = searchRequestsRepository.searchRequests
        .map { it.reversed() }
        .onStart { searchRequestsLceState.value = LceState.Loading }
        .onEach { searchRequestsLceState.value = LceState.Content }
        .catch { e -> searchRequestsLceState.value = LceState.Error(e.message) }

    private val searchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
    override val searchRequestsLce: Flow<LceState> = searchRequestsLceState.asStateFlow()

    override val popularSearchRequests: Flow<List<SearchRequest>> =
        searchRequestsRepository.popularSearchRequests
            .onStart { popularSearchRequestsLceState.value = LceState.Loading }
            .onEach { popularSearchRequestsLceState.value = LceState.Content }
            .catch { e -> popularSearchRequestsLceState.value = LceState.Error(e.message) }

    private val popularSearchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
    override val popularSearchRequestsLce: Flow<LceState> =
        popularSearchRequestsLceState.asStateFlow()

    override suspend fun onNewSearchRequest(request: String, resultsSize: Int) {
        if (resultsSize in 1..REQUIRED_RESULTS_FOR_CACHE) {
            eraseDuplicates(request)
            searchRequestsRepository.add(request)
        }
    }

    override suspend fun eraseAll() {
        searchRequestsRepository.eraseAll()
    }

    private suspend fun eraseDuplicates(newRequest: String) {
        val requestToCompare = newRequest.lowercase()
        searchRequestsRepository.searchRequests
            .firstOrNull()
            ?.forEach { searchRequest ->
                if (searchRequest.request.lowercase().contains(requestToCompare)) {
                    searchRequestsRepository.erase(searchRequest)
                }
            }
    }
}

internal const val REQUIRED_RESULTS_FOR_CACHE = 5