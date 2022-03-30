package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.SearchRequest
import com.ferelin.core.domain.repository.SearchRequestsRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface SearchRequestsUseCase {
  val searchRequests: Observable<List<SearchRequest>>
  val searchRequestsLce: Flow<LceState>
  val popularSearchRequests: Observable<List<SearchRequest>>
  val popularSearchRequestsLce: Flow<LceState>
  fun onNewSearchRequest(request: String, resultsSize: Int)
  fun eraseAll()
}

@Reusable
internal class SearchRequestsUseCaseImpl @Inject constructor(
  private val searchRequestsRepository: SearchRequestsRepository
) : SearchRequestsUseCase {
  override val searchRequests: Observable<List<SearchRequest>> =
    searchRequestsRepository.searchRequests
      .map { it.reversed() }
      .doOnSubscribe { searchRequestsLceState.value = LceState.Loading }
      .doOnEach { searchRequestsLceState.value = LceState.Content }
      .doOnError { e -> searchRequestsLceState.value = LceState.Error(e.message) }

  private val searchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
  override val searchRequestsLce: Flow<LceState> = searchRequestsLceState.asStateFlow()

  override val popularSearchRequests: Observable<List<SearchRequest>> =
    searchRequestsRepository.popularSearchRequests
      .doOnSubscribe { popularSearchRequestsLceState.value = LceState.Loading }
      .doOnEach { popularSearchRequestsLceState.value = LceState.Content }
      .doOnError { e -> popularSearchRequestsLceState.value = LceState.Error(e.message) }

  private val popularSearchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
  override val popularSearchRequestsLce: Flow<LceState> = popularSearchRequestsLceState.asStateFlow()

  override fun onNewSearchRequest(request: String, resultsSize: Int) {
    if (resultsSize in 1..REQUIRED_RESULTS_FOR_CACHE) {
      eraseDuplicates(request)
      searchRequestsRepository.add(request)
    }
  }

  override fun eraseAll() {
    searchRequestsRepository.eraseAll()
  }

  private fun eraseDuplicates(newRequest: String) {
    val requestToCompare = newRequest.lowercase()
    searchRequestsRepository.searchRequests.blockingFirst()
      .forEach { searchRequest ->
        if (searchRequest.request.lowercase().contains(requestToCompare)) {
          searchRequestsRepository.erase(searchRequest)
        }
      }
  }
}

internal const val REQUIRED_RESULTS_FOR_CACHE = 5