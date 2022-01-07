package com.ferelin.features.search.ui

import com.ferelin.features.search.domain.entity.SearchRequest

internal object SearchRequestMapper {
  fun map(search: SearchRequest): SearchViewData {
    return SearchViewData(search.id, search.request)
  }
}