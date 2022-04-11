package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.params.NewsParams
import com.ferelin.stockprice.shared.ui.viewModel.NewsStateUi
import com.ferelin.stockprice.shared.ui.viewModel.NewsViewModel
import com.ferelin.stockprice.sharedComposables.components.NewsItem
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun NewsScreenRoute(
    newsParams: NewsParams
) {
    val viewModelScope = rememberCoroutineScope()
    val viewModel: NewsViewModel = remember {
        ViewModelWrapper().viewModel(viewModelScope, newsParams)
    }
    val uiState by viewModel.uiState.collectAsState()

    NewsScreen(uiState)
}

@Composable
private fun NewsScreen(
    uiState: NewsStateUi
) {
    Crossfade(targetState = uiState.newsLce) { lceState ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.backgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            when (lceState) {
                is LceState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 6.dp,
                            end = 6.dp,
                            top = 12.dp,
                            bottom = 70.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = uiState.news) { newsViewData ->
                            NewsItem(
                                source = newsViewData.source,
                                url = newsViewData.sourceUrl,
                                date = newsViewData.date,
                                title = newsViewData.headline,
                                content = newsViewData.summary,
                                onUrlClick = { /**/ }
                            )
                        }
                    }
                }
                is LceState.Loading -> {
                    CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
                }
                is LceState.Error -> {
                    Text(
                        text = "Error",
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.textPrimary
                    )
                }
                else -> Unit
            }
        }
    }
}