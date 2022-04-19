@file:OptIn(ExperimentalAnimationApi::class)

package com.ferelin.features.home.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.APP_TOOLBAR_BASELINE
import com.ferelin.core.ui.HOME_ROUNDED_CORNER
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.AppFab
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.home.components.PreviewHolder
import com.ferelin.features.home.components.StockPreview
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreenRoute(
    onSettingsRoute: () -> Unit,
    onStocksRoute: () -> Unit,
    onCryptosRoute: () -> Unit,
    onNewsRoute: () -> Unit,
    onSupportRoute: () -> Unit
) {
    val viewModel = getViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onSettingsClick = onSettingsRoute,
        onStocksClick = onStocksRoute,
        onCryptosClick = onCryptosRoute,
        onNewsClick = onNewsRoute,
        onSupportClick = onSupportRoute
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onSettingsClick: () -> Unit,
    onStocksClick: () -> Unit,
    onCryptosClick: () -> Unit,
    onNewsClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        TopBar(onSettingsClick = onSettingsClick)
        BodyContent(
            stocks = uiState.stocks,
            stocksLce = uiState.stocksLce,
            onStocksClick = onStocksClick,
            onCryptosClick = onCryptosClick,
            onNewsClick = onNewsClick,
            onSupportClick = onSupportClick
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_TOOLBAR_BASELINE + HOME_ROUNDED_CORNER)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.backgroundPrimary,
                        AppTheme.colors.backgroundSecondary
                    ),
                    startY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE / 3).toPx() }
                )
            )
    ) {
        HomeHeader(onSettingsClick = onSettingsClick)
    }
}

@Composable
private fun BodyContent(
    modifier: Modifier = Modifier,
    stocks: List<HomeStockViewData>,
    stocksLce: LceState,
    onStocksClick: () -> Unit,
    onCryptosClick: () -> Unit,
    onNewsClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(top = APP_TOOLBAR_BASELINE)
            .fillMaxSize()
            .background(
                color = AppTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(topStart = HOME_ROUNDED_CORNER)
            )
    ) {
        MenuContent(
            stocks = stocks,
            stocksLce = stocksLce,
            onStocksClick = onStocksClick,
            onCryptosClick = onCryptosClick,
            onNewsClick = onNewsClick
        )
        AppFab(
            painter = painterResource(id = R.drawable.ic_info_24),
            contentDescription = stringResource(id = R.string.descriptionSelectSupport),
            onClick = onSupportClick
        )
    }
}

@Composable
private fun MenuContent(
    modifier: Modifier = Modifier,
    stocks: List<HomeStockViewData>,
    stocksLce: LceState,
    onStocksClick: () -> Unit,
    onCryptosClick: () -> Unit,
    onNewsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = APP_CONTENT_PADDING)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(HOME_ROUNDED_CORNER / 2))
        PreviewHolder(
            painter = painterResource(id = R.drawable.ic_stock_24),
            title = stringResource(id = R.string.hintStocks),
            contentDescription = stringResource(id = R.string.descriptionSelectStocks),
            onClick = onStocksClick
        ) {
            AnimatedContent(
                modifier = Modifier.padding(top = 8.dp),
                targetState = stocksLce
            ) { lceState ->
                when (lceState) {
                    is LceState.Loading -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppCircularProgressIndicator()
                        }
                    }
                    is LceState.Content -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = 10.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = stocks,
                                key = { it.id }
                            ) {
                                StockPreview(
                                    name = it.name,
                                    isFavorite = it.isFavorite,
                                    industry = it.industry,
                                    iconUrl = it.logoUrl
                                )
                            }
                        }
                    }
                    is LceState.Error -> {
                        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        PreviewHolder(
            painter = painterResource(id = R.drawable.ic_bitcoin_24),
            title = stringResource(id = R.string.hintCryptos),
            contentDescription = stringResource(id = R.string.descriptionSelectCryptos),
            onClick = onCryptosClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        PreviewHolder(
            painter = painterResource(id = R.drawable.ic_news_24),
            title = stringResource(id = R.string.hintNews),
            contentDescription = stringResource(id = R.string.descriptionSelectNews),
            onClick = onNewsClick
        )
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = APP_CONTENT_PADDING,
                end = APP_CONTENT_PADDING,
                top = APP_TOOLBAR_BASELINE / 4
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.titleHome),
            style = AppTheme.typography.title1,
            color = AppTheme.colors.textPrimary
        )
        ClickableIcon(
            painter = painterResource(id = R.drawable.ic_settings_24),
            contentDescription = stringResource(id = R.string.descriptionSettings),
            tint = AppTheme.colors.buttonPrimary,
            onClick = onSettingsClick
        )
    }
}

internal const val COMPANIES_FOR_PREVIEW = 5