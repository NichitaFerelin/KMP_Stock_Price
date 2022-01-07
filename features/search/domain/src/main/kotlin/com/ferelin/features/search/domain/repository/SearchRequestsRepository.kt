package com.ferelin.features.search.domain.repository

import com.ferelin.features.search.domain.entity.SearchRequest
import kotlinx.coroutines.flow.Flow

interface SearchRequestsRepository {
  val searchRequests: Flow<List<SearchRequest>>
  val popularSearchRequests: Flow<List<SearchRequest>>
  suspend fun add(request: String)
  suspend fun eraseAll()
}