package com.ferelin.stockprice.sharedComposables.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.viewData.SearchViewData

@Composable
fun SearchRequestsSection(
  searchRequests: List<SearchViewData>,
  searchRequestsLce: LceState,
  popularSearchRequests: List<SearchViewData>,
  popularSearchRequestsLce: LceState,
  onTickerClick: (SearchViewData) -> Unit
) {
  Spacer(modifier = Modifier.height(16.dp))
  SearchRequests(
    title = "Popular search requests"/*stringResource(R.string.titlePopularRequests)*/,
    searchRequests = popularSearchRequests,
    searchRequestsLce = popularSearchRequestsLce,
    onTickerClick = onTickerClick
  )
  Spacer(modifier = Modifier.height(16.dp))
  SearchRequests(
    title = "You've searched for"/*stringResource(R.string.titleYourSearches)*/,
    searchRequests = searchRequests,
    searchRequestsLce = searchRequestsLce,
    onTickerClick = onTickerClick
  )
}