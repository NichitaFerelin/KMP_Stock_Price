package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.searchRequest.SearchRequestDBO
import com.ferelin.core.data.entity.searchRequest.SearchRequestDao
import com.ferelin.core.data.mapper.SearchRequestMapper
import com.ferelin.core.domain.entity.SearchId
import com.ferelin.core.domain.entity.SearchRequest
import com.ferelin.core.domain.repository.SearchRequestsRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class SearchRequestsRepositoryImpl @Inject constructor(
  private val dao: SearchRequestDao
) : SearchRequestsRepository {
  override val searchRequests: Observable<List<SearchRequest>>
    get() = dao.getAll()
      .distinctUntilChanged()
      .map { it.map(SearchRequestMapper::map) }

  override val popularSearchRequests: Observable<List<SearchRequest>>
    get() = Mock.popularSearchRequests()

  override fun add(request: String) {
    dao.insert(
      searchRequestsDBO = SearchRequestDBO(request = request)
    )
  }

  override fun erase(searchRequest: SearchRequest) {
    dao.erase(
      searchRequestDBO = SearchRequestMapper.map(searchRequest)
    )
  }

  override fun eraseAll() {
    dao.eraseAll()
  }
}

private object Mock {
  fun popularSearchRequests(): Observable<List<SearchRequest>> = Observable.just(
    listOf(
      SearchRequest(SearchId(0), "Apple"),
      SearchRequest(SearchId(1), "Microsoft Corp"),
      SearchRequest(SearchId(2), "Amazon.com"),
      SearchRequest(SearchId(3), "Alphabet"),
      SearchRequest(SearchId(4), "JD.com"),
      SearchRequest(SearchId(5), "Tesla"),
      SearchRequest(SearchId(6), "Facebook"),
      SearchRequest(SearchId(7), "Telefonaktiebolaget"),
      SearchRequest(SearchId(8), "NVIDIA"),
      SearchRequest(SearchId(9), "Beigene"),
      SearchRequest(SearchId(10), "Intel"),
      SearchRequest(SearchId(11), "Netflix"),
      SearchRequest(SearchId(12), "Adobe"),
      SearchRequest(SearchId(13), "Cisco"),
      SearchRequest(SearchId(14), "Yandex"),
      SearchRequest(SearchId(15), "Zoom"),
      SearchRequest(SearchId(16), "Starbucks"),
      SearchRequest(SearchId(17), "Charter"),
      SearchRequest(SearchId(18), "Sanofi"),
      SearchRequest(SearchId(19), "Amgen"),
      SearchRequest(SearchId(20), "Pepsi"),
    )
  )
}