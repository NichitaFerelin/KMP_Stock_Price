package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.SearchRequest
import kotlinx.coroutines.flow.Flow

interface SearchRequestsRepository {
  val searchRequests: Flow<List<SearchRequest>>
  val popularSearchRequests: Flow<List<SearchRequest>>
  suspend fun add(request: String)
  suspend fun erase(searchRequest: SearchRequest)
  suspend fun eraseAll()
}