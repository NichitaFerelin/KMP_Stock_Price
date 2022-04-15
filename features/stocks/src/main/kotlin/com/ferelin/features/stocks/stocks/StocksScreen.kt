@file:OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)

package com.ferelin.features.stocks.stocks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.stocks.components.DefaultStockItem
import com.ferelin.features.stocks.components.FavoriteStockItem
import com.ferelin.features.stocks.components.StocksSectionHeader
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun StocksScreenRoute(
    onSearchRoute: () -> Unit,
    onStockRoute: (CompanyId) -> Unit,
    onBackRoute: () -> Unit
) {
    val viewModel = getViewModel<StocksViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    StocksScreen(
        uiState = uiState,
        onSearchClick = onSearchRoute,
        onBackClick = onBackRoute,
        onStockClick = { onStockRoute(it.id) },
        onFavoriteIconClick = viewModel::switchFavorite
    )
}

@Composable
private fun StocksScreen(
    uiState: StocksUiState,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onStockClick: (StockViewData) -> Unit,
    onFavoriteIconClick: (StockViewData) -> Unit
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
                onSearchClick = onSearchClick,
                onBackClick = onBackClick
            )
            BodyContent(
                state = listState,
                stocks = uiState.stocks,
                stocksLce = uiState.stocksLce,
                favoriteStocks = uiState.favoriteStocks,
                favoriteStocksLce = uiState.favoriteStocksLce,
                onFavoriteIconClick = onFavoriteIconClick,
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
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackField(onBackClick = onBackClick)
            Text(
                text = stringResource(id = R.string.titleStocks),
                style = AppTheme.typography.title1,
                color = AppTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SearchField(onSearchClick = onSearchClick)
    }
}

@Composable
private fun BodyContent(
    state: LazyListState,
    stocks: List<StockViewData>,
    stocksLce: LceState,
    favoriteStocks: List<StockViewData>,
    favoriteStocksLce: LceState,
    onStockClick: (StockViewData) -> Unit,
    onFavoriteIconClick: (StockViewData) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = state,
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 50.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            FavoritesSection(
                stocks = favoriteStocks,
                lce = favoriteStocksLce,
                onStockClick = onStockClick,
                onFavoriteIconClick = onFavoriteIconClick
            )
        }
        item {
            StocksSectionHeader(
                title = stringResource(id = R.string.titleSectionStocks)
            )
        }
        item {
            AnimatedVisibility(
                visible = stocksLce is LceState.Loading || stocksLce is LceState.Error
            ) {
                when (stocksLce) {
                    is LceState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppCircularProgressIndicator()
                        }
                    }
                    is LceState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.errorDownload),
                                style = AppTheme.typography.body2,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
        items(
            count = stocks.size,
            key = { stocks[it].id.value }
        ) { index ->
            val stock = stocks[index]

            DefaultStockItem(
                ticker = stock.ticker,
                name = stock.name,
                logoUrl = stock.logoUrl,
                index = index,
                onClick = { onStockClick(stock) },
                onFavoriteIconClick = { onFavoriteIconClick(stock) }
            )
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        elevation = 1.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = AppTheme.colors.backgroundPrimary,
        onClick = onSearchClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_search_17x18),
                tint = AppTheme.colors.buttonPrimary,
                contentDescription = stringResource(id = R.string.descriptionSearch)
            )
            Spacer(modifier = Modifier.width(16.dp))
            ConstrainedText(
                text = stringResource(id = R.string.descriptionSearch),
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textPrimary
            )
        }
    }
}

@Composable
private fun BackField(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onBackClick)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back_24),
            tint = AppTheme.colors.buttonPrimary,
            contentDescription = stringResource(id = R.string.descriptionBack)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(id = R.string.hintBack),
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textPrimary
        )
    }
}

@Composable
private fun FavoritesSection(
    stocks: List<StockViewData>,
    lce: LceState,
    onStockClick: (StockViewData) -> Unit,
    onFavoriteIconClick: (StockViewData) -> Unit
) {
    val listState = rememberLazyListState()
    var previousListSize by remember { mutableStateOf(stocks.size) }

    LaunchedEffect(key1 = stocks.size) {
        if (previousListSize < stocks.size) {
            listState.animateScrollToItem(0)
        }
        previousListSize = stocks.size
    }

    AnimatedVisibility(
        visible = lce is LceState.Loading || stocks.isNotEmpty()
    ) {
        Column {
            StocksSectionHeader(
                title = stringResource(id = R.string.titleSectionFavorites)
            )
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedContent(
                targetState = lce
            ) { lceState ->
                when (lceState) {
                    is LceState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppCircularProgressIndicator()
                        }
                    }
                    is LceState.Content -> {
                        LazyRow(
                            state = listState,
                            contentPadding = PaddingValues(
                                horizontal = APP_CONTENT_PADDING
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = stocks,
                            ) {
                                FavoriteStockItem(
                                    name = it.name,
                                    logoUrl = it.logoUrl,
                                    industry = it.industry,
                                    onClick = { onStockClick(it) },
                                    onFavoriteIconClick = { onFavoriteIconClick(it) }
                                )
                            }
                        }
                    }
                    is LceState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.errorDownload),
                                style = AppTheme.typography.body2,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}