package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.SearchRequest
import kotlinx.coroutines.flow.Flow

interface SearchRequestsRepository {
  val searchRequests: Flow<List<SearchRequest>>
  val popularSearchRequests: Flow<List<SearchRequest>>
  suspend fun add(request: String)
  suspend fun erase(searchRequest: SearchRequest)
  suspend fun eraseAll()
}