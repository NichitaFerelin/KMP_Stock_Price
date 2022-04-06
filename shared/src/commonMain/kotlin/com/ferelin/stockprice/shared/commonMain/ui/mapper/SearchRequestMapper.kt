package com.ferelin.stockprice.shared.commonMain.ui.mapper

import com.ferelin.stockprice.shared.commonMain.domain.entity.SearchRequest
import com.ferelin.stockprice.shared.commonMain.ui.viewData.SearchViewData

object SearchRequestMapper {
  fun map(search: SearchRequest): SearchViewData {
    return SearchViewData(search.id, search.request)
  }
}