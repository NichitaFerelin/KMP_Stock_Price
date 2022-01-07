package com.ferelin.features.search.data.mapper

import com.ferelin.features.search.data.entity.SearchRequestsDBO
import com.ferelin.features.search.domain.entity.SearchId
import com.ferelin.features.search.domain.entity.SearchRequest

internal object SearchRequestMapper {
  fun map(searchRequestsDBO: SearchRequestsDBO): SearchRequest {
    return SearchRequest(
      id = SearchId(searchRequestsDBO.id),
      request = searchRequestsDBO.request
    )
  }
}