package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.searchRequest.SearchRequestDBO
import com.ferelin.core.domain.entity.SearchId
import com.ferelin.core.domain.entity.SearchRequest

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