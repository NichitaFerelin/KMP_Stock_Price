package com.ferelin.features.marketNews.marketNews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.APP_TOOLBAR_BASELINE
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.AppFab
import com.ferelin.core.ui.components.BackField
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.marketNews.components.MarketNewsItem
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun MarketNewsScreenRoute(
    onBackRoute: () -> Unit
) {
    val viewModel = getViewModel<MarketNewsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    MarketNewsScreen(
        uiState = uiState,
        onMarketNewsRefresh = viewModel::fetchNews,
        onBackClick = onBackRoute
    )
}

@Composable
private fun MarketNewsScreen(
    uiState: MarketNewsUiState,
    onMarketNewsRefresh: () -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppTheme.colors.backgroundPrimary)
    ) {
        Column {
            TopBar(onBackClick = onBackClick)

            when (uiState.marketNewsLce) {
                is LceState.Content -> {
                    MarketNewsSection(
                        listState = listState,
                        marketNews = uiState.marketNews,
                        marketNewsFetchLce = uiState.marketNewsFetchLce,
                        onMarketNewsRefresh = onMarketNewsRefresh
                    )
                }
                is LceState.Loading -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    AppCircularProgressIndicator()
                }
                is LceState.Error -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.errorDownload),
                        style = AppTheme.typography.title2,
                        color = AppTheme.colors.textPrimary
                    )
                }
                else -> Unit
            }
        }
        AppFab(
            painter = painterResource(id = R.drawable.ic_up_24),
            contentDescription = stringResource(id = R.string.descriptionScrollUp),
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_TOOLBAR_BASELINE / 2)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.backgroundPrimary,
                        AppTheme.colors.backgroundSecondary
                    ),
                    startY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE / 4).toPx() }
                )
            )
            .padding(
                start = APP_CONTENT_PADDING,
                end = APP_CONTENT_PADDING,
                top = 10.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BackField(onBackClick = onBackClick)
            Text(
                text = stringResource(id = R.string.titleMarketNews),
                style = AppTheme.typography.title1,
                color = AppTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun MarketNewsSection(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    marketNews: List<MarketNewsViewData>,
    marketNewsFetchLce: LceState,
    onMarketNewsRefresh: () -> Unit
) {
    SwipeRefresh(
        modifier = modifier.fillMaxWidth(),
        state = rememberSwipeRefreshState(
            isRefreshing = marketNewsFetchLce is LceState.Loading
        ),
        onRefresh = onMarketNewsRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = AppTheme.colors.backgroundSecondary,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = APP_CONTENT_PADDING),
            state = listState,
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = 60.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(marketNews) {
                MarketNewsItem(
                    headline = it.headline,
                    summary = it.summary,
                    sourceUrl = it.sourceUrl,
                    imageUrl = it.imageUrl,
                    category = it.category,
                    date = it.date
                )
            }
        }
    }
}