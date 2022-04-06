package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.shared.domain.entity.SearchRequest
import com.ferelin.stockprice.shared.ui.viewData.SearchViewData

object SearchRequestMapper {
  fun map(search: SearchRequest): SearchViewData {
    return SearchViewData(search.id, search.request)
  }
}