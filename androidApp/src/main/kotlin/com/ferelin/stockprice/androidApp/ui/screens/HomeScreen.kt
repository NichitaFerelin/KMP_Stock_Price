@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.stockprice.androidApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.androidApp.R
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.shared.ui.viewModel.*
import com.ferelin.stockprice.sharedComposables.components.HomeTab
import com.ferelin.stockprice.sharedComposables.components.TopSearchField
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel

@Composable
internal fun HomeScreenRoute(
  onSearchRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: HomeViewModel = remember { viewModelWrapper.viewModel() }
  val uiState by viewModel.uiState.collectAsState()

  HomeScreen(
    uiState = uiState,
    onScreenSelected = viewModel::onScreenSelected,
    onSearchClick = onSearchRoute,
    onCryptosRoute = { CryptosScreenRoute() },
    onStocksRoute = { StocksScreenRoute(onStockRoute = onStockRoute) },
    onFavouriteStocksRoute = { FavouriteStocksScreenRoute(onStockRoute = onStockRoute) }
  )
}

@Composable
private fun HomeScreen(
  uiState: HomeStateUi,
  onScreenSelected: (Int) -> Unit,
  onSearchClick: () -> Unit,
  onCryptosRoute: @Composable () -> Unit,
  onStocksRoute: @Composable () -> Unit,
  onFavouriteStocksRoute: @Composable () -> Unit
) {
  val pagerState = rememberPagerState(initialPage = uiState.selectedScreenIndex)
  LaunchedEffect(key1 = uiState.selectedScreenIndex) {
    pagerState.animateScrollToPage(uiState.selectedScreenIndex)
  }
  LaunchedEffect(key1 = pagerState) {
    snapshotFlow { pagerState.currentPage }
      .onEach { onScreenSelected.invoke(it) }
      .launchIn(this)
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = stringResource(id = R.string.titleHome),
      style = AppTheme.typography.title1,
      color = AppTheme.colors.textPrimary
    )
    Spacer(modifier = Modifier.height(12.dp))
    TopSearchField(onClick = onSearchClick)
    Spacer(modifier = Modifier.height(12.dp))
    PagerTabs(
      modifier = Modifier.fillMaxWidth(),
      pagerState = pagerState,
      onTabClick = onScreenSelected
    )
    ScreensPager(
      pagerState = pagerState,
      onCryptosRoute = onCryptosRoute,
      onStocksRoute = onStocksRoute,
      onFavouriteStocksRoute = onFavouriteStocksRoute
    )
  }
}


@Composable
private fun PagerTabs(
  modifier: Modifier = Modifier,
  pagerState: PagerState,
  onTabClick: (Int) -> Unit
) {
  TabRow(
    modifier = modifier,
    backgroundColor = AppTheme.colors.backgroundPrimary,
    selectedTabIndex = pagerState.currentPage,
    indicator = { tabPositions ->
      TabRowDefaults.Indicator(
        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
      )
    }
  ) {
    repeat(HOME_TOTAL_SCREENS) { index ->
      when (index) {
        SCREEN_CRYPTOS_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleCryptos),
            isSelected = pagerState.currentPage == SCREEN_CRYPTOS_INDEX,
            onClick = { onTabClick(SCREEN_CRYPTOS_INDEX) }
          )
        }
        SCREEN_STOCKS_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleStocks),
            isSelected = pagerState.currentPage == SCREEN_STOCKS_INDEX,
            onClick = { onTabClick(SCREEN_STOCKS_INDEX) }
          )
        }
        SCREEN_FAVOURITE_STOCKS_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleFavourite),
            isSelected = pagerState.currentPage == SCREEN_FAVOURITE_STOCKS_INDEX,
            onClick = { onTabClick(SCREEN_FAVOURITE_STOCKS_INDEX) }
          )
        }
        else -> error("There is no tab for screen index [$index] ")
      }
    }
  }
}

@Composable
private fun ScreensPager(
  pagerState: PagerState,
  onCryptosRoute: @Composable () -> Unit,
  onStocksRoute: @Composable () -> Unit,
  onFavouriteStocksRoute: @Composable () -> Unit
) {
  HorizontalPager(
    count = HOME_TOTAL_SCREENS,
    state = pagerState
  ) { pageIndex ->
    when (pageIndex) {
      SCREEN_CRYPTOS_INDEX -> onCryptosRoute()
      SCREEN_STOCKS_INDEX -> onStocksRoute()
      SCREEN_FAVOURITE_STOCKS_INDEX -> onFavouriteStocksRoute()
      else -> error("There is no screen for index [$pageIndex]")
    }
  }
}