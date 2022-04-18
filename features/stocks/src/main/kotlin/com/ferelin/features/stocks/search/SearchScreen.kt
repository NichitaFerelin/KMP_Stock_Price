package com.ferelin.features.stocks.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.APP_TOOLBAR_BASELINE
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.AppFab
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.stocks.components.ExpandedStockItem
import com.ferelin.features.stocks.components.SearchRequestsSection
import com.ferelin.features.stocks.components.SearchTextField
import com.ferelin.features.stocks.stocks.StockViewData
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchScreenRoute(
    onStockRoute: (CompanyId) -> Unit,
    onBackRoute: () -> Unit
) {
    val viewModel = getViewModel<SearchViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    SearchScreen(
        uiState = uiState,
        onSearchRequestsChanged = viewModel::onSearchTextChanged,
        onSearchTickerClick = viewModel::onTickerClick,
        onStockClick = { onStockRoute(it.id) },
        onBackClick = onBackRoute
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onSearchRequestsChanged: (String) -> Unit,
    onSearchTickerClick: (SearchViewData) -> Unit,
    onStockClick: (StockViewData) -> Unit,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.backgroundPrimary),
        ) {
            TopBar(
                uiState = uiState,
                onSearchRequestsChanged = onSearchRequestsChanged,
                onBackClick = onBackClick
            )
            BodyContent(
                stocksListState = listState,
                searchRequests = uiState.searchRequests,
                searchRequestsLce = uiState.searchRequestsLce,
                popularSearchRequests = uiState.popularSearchRequests,
                popularSearchRequestsLce = uiState.popularSearchRequestsLce,
                searchResults = uiState.searchResults,
                searchResultsLce = uiState.searchResultsLce,
                onSearchTickerClick = onSearchTickerClick,
                onStockClick = onStockClick
            )
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
    uiState: SearchUiState,
    onSearchRequestsChanged: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(APP_TOOLBAR_BASELINE)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.backgroundPrimary,
                        AppTheme.colors.backgroundSecondary
                    ),
                    startY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE / 2).toPx() }
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
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(id = R.string.titleSearch),
                style = AppTheme.typography.title1,
                color = AppTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SearchTextField(
            searchRequest = uiState.inputSearchRequest,
            onSearchRequestsChanged = onSearchRequestsChanged,
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun BodyContent(
    stocksListState: LazyListState,
    searchRequests: List<SearchViewData>,
    searchRequestsLce: LceState,
    popularSearchRequests: List<SearchViewData>,
    popularSearchRequestsLce: LceState,
    searchResults: List<StockViewData>,
    searchResultsLce: LceState,
    onSearchTickerClick: (SearchViewData) -> Unit,
    onStockClick: (StockViewData) -> Unit
) {
    when (searchResultsLce) {
        is LceState.Content -> {
            if (searchResults.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(id = R.string.descriptionNoSearchResults),
                        style = AppTheme.typography.title2,
                        color = AppTheme.colors.textPrimary
                    )
                }
            } else {
                StocksBody(
                    listState = stocksListState,
                    stocks = searchResults,
                    onStockClick = onStockClick
                )
            }
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(id = R.string.errorDownload),
                    style = AppTheme.typography.title2,
                    color = AppTheme.colors.textPrimary
                )
            }
        }
        is LceState.None -> {
            SearchRequestsBody(
                searchRequests = searchRequests,
                searchRequestsLce = searchRequestsLce,
                popularSearchRequests = popularSearchRequests,
                popularSearchRequestsLce = popularSearchRequestsLce,
                onSearchTickerClick = onSearchTickerClick
            )
        }
    }
}

@Composable
private fun SearchRequestsBody(
    modifier: Modifier = Modifier,
    searchRequests: List<SearchViewData>,
    searchRequestsLce: LceState,
    popularSearchRequests: List<SearchViewData>,
    popularSearchRequestsLce: LceState,
    onSearchTickerClick: (SearchViewData) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        SearchRequestsSection(
            title = stringResource(id = R.string.titlePopularRequests),
            searchRequests = popularSearchRequests,
            searchRequestsLce = popularSearchRequestsLce,
            onSearchTickerClick = onSearchTickerClick
        )
        Spacer(modifier = Modifier.height(12.dp))
        SearchRequestsSection(
            title = stringResource(id = R.string.titleYourSearches),
            searchRequests = searchRequests,
            searchRequestsLce = searchRequestsLce,
            onSearchTickerClick = onSearchTickerClick
        )
    }
}

@Composable
private fun StocksBody(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    stocks: List<StockViewData>,
    onStockClick: (StockViewData) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 50.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            count = stocks.size,
            key = { stocks[it].id.value }
        ) { index ->
            val stock = stocks[index]

            ExpandedStockItem(
                ticker = stock.ticker,
                name = stock.name,
                industry = stock.industry,
                logoUrl = stock.logoUrl,
                index = index,
                onClick = { onStockClick(stock) },
            )
        }
    }
}