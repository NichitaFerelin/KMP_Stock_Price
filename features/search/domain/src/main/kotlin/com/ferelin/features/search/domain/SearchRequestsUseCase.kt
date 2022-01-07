package com.ferelin.features.search.domain

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.ExternalScope
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.core.domain.entities.repository.AuthUserStateRepository
import com.ferelin.features.search.domain.entity.SearchRequest
import com.ferelin.features.search.domain.repository.SearchRequestsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface SearchRequestsUseCase {
  val searchRequests: Flow<List<SearchRequest>>
  val searchRequestsLce: Flow<LceState>
  val popularSearchRequests: Flow<List<SearchRequest>>
  val popularSearchRequestsLce: Flow<LceState>
  suspend fun add(request: String)

  companion object {
    const val REQUIRED_RESULTS_FOR_CACHE = 5
  }
}

internal class SearchRequestsUseCaseImpl @Inject constructor(
  private val searchRequestsRepository: SearchRequestsRepository,
  authUserStateRepository: AuthUserStateRepository,
  @ExternalScope scope: CoroutineScope,
  dispatchersProvider: DispatchersProvider
) : SearchRequestsUseCase {
  init {
    authUserStateRepository.userAuthenticated
      .filter { !it }
      .onEach { searchRequestsRepository.eraseAll() }
      .launchIn(scope)
  }

  override val searchRequests: Flow<List<SearchRequest>> = searchRequestsRepository.searchRequests
    .onStart { searchRequestsLceState.value = LceState.Loading }
    .onEach { searchRequestsLceState.value = LceState.Content }
    .catch { e -> searchRequestsLceState.value = LceState.Error(e.message) }
    .flowOn(dispatchersProvider.IO)

  private val searchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
  override val searchRequestsLce: Flow<LceState> = searchRequestsLceState.asStateFlow()

  override val popularSearchRequests: Flow<List<SearchRequest>> = searchRequestsRepository.popularSearchRequests
    .onStart { popularSearchRequestsLceState.value = LceState.Loading }
    .onEach { popularSearchRequestsLceState.value = LceState.Content }
    .catch { e -> popularSearchRequestsLceState.value = LceState.Error(e.message) }
    .flowOn(dispatchersProvider.IO)

  private val popularSearchRequestsLceState = MutableStateFlow<LceState>(LceState.None)
  override val popularSearchRequestsLce: Flow<LceState> = popularSearchRequestsLceState.asStateFlow()

  override suspend fun add(request: String) {
    searchRequestsRepository.add(request)
  }
}