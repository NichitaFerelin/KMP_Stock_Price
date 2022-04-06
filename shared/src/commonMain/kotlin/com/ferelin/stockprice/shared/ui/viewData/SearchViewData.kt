package com.ferelin.stockprice.shared.ui.viewData

import com.ferelin.stockprice.androidApp.domain.entity.SearchId

data class SearchViewData(
  val id: SearchId,
  val text: String
)