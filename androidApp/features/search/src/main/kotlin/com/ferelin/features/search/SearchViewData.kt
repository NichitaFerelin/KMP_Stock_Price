package com.ferelin.features.search

import com.ferelin.core.domain.entity.SearchId

data class SearchViewData(
  val id: SearchId,
  val text: String
)