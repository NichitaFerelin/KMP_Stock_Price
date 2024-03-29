package com.ferelin.stockprice.shared.data.repository

import com.ferelin.stockprice.shared.data.entity.searchRequest.SearchRequestDao
import com.ferelin.stockprice.shared.data.mapper.SearchRequestMapper
import com.ferelin.stockprice.shared.domain.entity.SearchId
import com.ferelin.stockprice.shared.domain.entity.SearchRequest
import com.ferelin.stockprice.shared.domain.repository.SearchRequestsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class SearchRequestsRepositoryImpl(
    private val dao: SearchRequestDao
) : SearchRequestsRepository {
    override val searchRequests: Flow<List<SearchRequest>>
        get() = dao.getAll()
            .distinctUntilChanged()
            .map { it.map(SearchRequestMapper::map) }

    override val popularSearchRequests: Flow<List<SearchRequest>>
        get() = Mock.popularSearchRequests()

    override suspend fun add(request: String) {
        dao.insert(request)
    }

    override suspend fun erase(searchRequest: SearchRequest) {
        dao.eraseBy(searchRequest.id.value)
    }

    override suspend fun eraseAll() {
        dao.eraseAll()
    }
}

private object Mock {
    fun popularSearchRequests(): Flow<List<SearchRequest>> = flow {
        emit(
            value = listOf(
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
}