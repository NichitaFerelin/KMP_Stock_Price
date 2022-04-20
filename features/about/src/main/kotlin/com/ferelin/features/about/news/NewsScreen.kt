package com.ferelin.features.about.news

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.AppFab
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.utils.openUrl
import com.ferelin.features.about.components.NewsItem
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsScreenRoute(
    companyId: Int
) {
    val viewModel = getViewModel<NewsViewModel>(
        parameters = { parametersOf(companyId) }
    )
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    NewsScreen(
        uiState = uiState,
        onNewsRefresh = viewModel::fetchNews,
        onUrlClick = { context.openUrl(it) }
    )
}

@Composable
private fun NewsScreen(
    uiState: NewsUiState,
    onNewsRefresh: () -> Unit,
    onUrlClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppTheme.colors.backgroundPrimary)
    ) {
        BodyContent(
            listState = listState,
            news = uiState.news,
            newsLce = uiState.newsLce,
            newsFetchLceState = uiState.newsFetchLce,
            onNewsRefresh = onNewsRefresh,
            onUrlClick = onUrlClick
        )
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
private fun BodyContent(
    listState: LazyListState,
    news: List<NewsViewData>,
    newsLce: LceState,
    newsFetchLceState: LceState,
    onNewsRefresh: () -> Unit,
    onUrlClick: (String) -> Unit
) {
    when (newsLce) {
        is LceState.Content -> {
            NewsSection(
                news = news,
                listState = listState,
                newsFetchLceState = newsFetchLceState,
                onNewsRefresh = onNewsRefresh,
                onUrlClick = onUrlClick
            )
        }
        is LceState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AppCircularProgressIndicator()
            }
        }
        is LceState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.errorDownload),
                    style = AppTheme.typography.title2,
                    color = AppTheme.colors.textPrimary
                )
            }
        }
        else -> Unit
    }
}

@Composable
private fun NewsSection(
    modifier: Modifier = Modifier,
    news: List<NewsViewData>,
    listState: LazyListState,
    newsFetchLceState: LceState,
    onNewsRefresh: () -> Unit,
    onUrlClick: (String) -> Unit
) {
    SwipeRefresh(
        modifier = modifier.fillMaxWidth(),
        state = rememberSwipeRefreshState(
            isRefreshing = newsFetchLceState is LceState.Loading
        ),
        onRefresh = onNewsRefresh,
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
            state = listState,
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 50.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(news) {
                NewsItem(
                    source = it.source,
                    url = it.sourceUrl,
                    date = it.date,
                    title = it.headline,
                    content = it.summary,
                    onUrlClick = { onUrlClick(it.sourceUrl) }
                )
            }
        }
    }
}