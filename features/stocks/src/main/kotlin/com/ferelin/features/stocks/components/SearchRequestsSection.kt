package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.stocks.search.SearchViewData

@Composable
internal fun SearchRequestsSection(
    modifier: Modifier = Modifier,
    title: String,
    searchRequests: List<SearchViewData>,
    searchRequestsLce: LceState,
    onSearchTickerClick: (SearchViewData) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = APP_CONTENT_PADDING),
            text = title,
            style = AppTheme.typography.title2,
            color = AppTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        when (searchRequestsLce) {
            is LceState.Content -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = APP_CONTENT_PADDING),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(searchRequests) {
                        SearchTicker(
                            text = it.text,
                            onClick = { onSearchTickerClick(it) }
                        )
                    }
                }
            }
            is LceState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SEARCH_TICKER_HEIGHT),
                    contentAlignment = Alignment.Center
                ) {
                    AppCircularProgressIndicator()
                }
            }
            is LceState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SEARCH_TICKER_HEIGHT / 2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.errorDownload),
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.textPrimary
                    )
                }
            }
            else -> Unit
        }
    }
}