package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.SearchRequest
import io.reactivex.rxjava3.core.Observable

interface SearchRequestsRepository {
  val searchRequests: Observable<List<SearchRequest>>
  val popularSearchRequests: Observable<List<SearchRequest>>
  fun add(request: String)
  fun erase(searchRequest: SearchRequest)
  fun eraseAll()
}