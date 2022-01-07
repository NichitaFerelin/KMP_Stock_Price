package com.ferelin.features.search.ui

import com.ferelin.core.ui.view.adapter.ViewDataType
import com.ferelin.features.search.domain.entity.SearchId

internal data class SearchViewData(
  val id: SearchId,
  val text: String
) : ViewDataType(TICKER_VIEW_TYPE) {
  override fun getUniqueId(): Long = id.value.toLong()
}