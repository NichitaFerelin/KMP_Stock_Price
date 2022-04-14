package com.ferelin.core.data.mapper

import com.ferelin.core.domain.entity.SearchId
import com.ferelin.core.domain.entity.SearchRequest
import stockprice.SearchRequestDBO

internal object SearchRequestMapper {
    fun map(searchRequestsDBO: SearchRequestDBO): SearchRequest {
        return SearchRequest(
            id = SearchId(searchRequestsDBO.id),
            request = searchRequestsDBO.request
        )
    }

    fun map(searchRequest: SearchRequest): SearchRequestDBO {
        return SearchRequestDBO(
            id = searchRequest.id.value,
            request = searchRequest.request
        )
    }
}