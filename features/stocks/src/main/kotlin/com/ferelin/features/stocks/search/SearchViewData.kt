package com.ferelin.features.stocks.search

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.SearchId
import com.ferelin.core.domain.entity.SearchRequest

@Immutable
internal data class SearchViewData(
    val id: SearchId,
    val text: String
)

internal fun SearchRequest.toSearchViewData() : SearchViewData {
    return SearchViewData(id, request)
}