package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.desktopApp.ui.components.APP_START_PADDING
import com.ferelin.stockprice.desktopApp.ui.components.APP_TOP_PADDING
import com.ferelin.stockprice.desktopApp.ui.components.NavigationBarItem
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.*
import com.ferelin.stockprice.sharedComposables.components.TopSearchField
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
internal fun HomeScreenRoute(
    onSearchRoute: () -> Unit,
    onStockRoute: (StockViewData) -> Unit
) {
    val viewModelScope = rememberCoroutineScope()
    val viewModel: HomeViewModel = remember { ViewModelWrapper().viewModel(viewModelScope) }
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onScreenSelected = viewModel::onScreenSelected,
        onSearchRoute = onSearchRoute,
        onStockClick = onStockRoute
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeStateUi,
    onScreenSelected: (Int) -> Unit,
    onSearchRoute: () -> Unit,
    onStockClick: (StockViewData) -> Unit
) {
    Row {
        NavigationBar(
            modifier = Modifier.padding(top = APP_TOP_PADDING),
            selectedScreenIndex = uiState.selectedScreenIndex,
            onScreenSelected = onScreenSelected
        )
        Column {
            TopSearchField(
                modifier = Modifier.padding(top = APP_TOP_PADDING),
                onClick = onSearchRoute
            )
            Spacer(modifier = Modifier.height(6.dp))
            SelectedScreenContent(
                selectedScreenIndex = uiState.selectedScreenIndex,
                onStockClick = onStockClick
            )
        }
    }
}

@Composable
private fun NavigationBar(
    modifier: Modifier = Modifier,
    selectedScreenIndex: Int,
    onScreenSelected: (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = APP_START_PADDING),
            text = "Home",
            style = AppTheme.typography.title1,
            color = AppTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))
        repeat(HOME_TOTAL_SCREENS) { screenIndex ->
            when (screenIndex) {
                SCREEN_STOCKS_INDEX -> {
                    NavigationBarItem(
                        title = "Stocks",
                        isSelected = selectedScreenIndex == SCREEN_STOCKS_INDEX,
                        onClick = { onScreenSelected(SCREEN_STOCKS_INDEX) }
                    )
                }
                SCREEN_FAVOURITE_STOCKS_INDEX -> {
                    NavigationBarItem(
                        title = "Favourite Stocks",
                        isSelected = selectedScreenIndex == SCREEN_FAVOURITE_STOCKS_INDEX,
                        onClick = { onScreenSelected(SCREEN_FAVOURITE_STOCKS_INDEX) }
                    )
                }
                SCREEN_CRYPTOS_INDEX -> {
                    NavigationBarItem(
                        title = "Cryptos",
                        isSelected = selectedScreenIndex == SCREEN_CRYPTOS_INDEX,
                        onClick = { onScreenSelected(SCREEN_CRYPTOS_INDEX) }
                    )
                }
                else -> error("No screen for index [$screenIndex]")
            }
        }
    }
}

@Composable
private fun SelectedScreenContent(
    selectedScreenIndex: Int,
    onStockClick: (StockViewData) -> Unit
) {
    when (selectedScreenIndex) {
        SCREEN_STOCKS_INDEX -> StocksScreenRoute(onStockRoute = onStockClick)
        SCREEN_FAVOURITE_STOCKS_INDEX -> FavouriteStocksScreenRoute(onStockRoute = onStockClick)
        SCREEN_CRYPTOS_INDEX -> CryptosScreenRoute()
        else -> error("No screen for index [$selectedScreenIndex]")
    }
}