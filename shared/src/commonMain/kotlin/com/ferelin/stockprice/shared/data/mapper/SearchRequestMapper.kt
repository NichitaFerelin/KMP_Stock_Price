package com.ferelin.stockprice.shared.data.mapper

import com.ferelin.stockprice.db.SearchRequestDBO
import com.ferelin.stockprice.shared.domain.entity.SearchId
import com.ferelin.stockprice.shared.domain.entity.SearchRequest

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