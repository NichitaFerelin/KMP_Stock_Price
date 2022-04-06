@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.features.home.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.SearchField
import com.ferelin.stockprice.ui.viewData.StockViewData
import com.ferelin.features.home.R
import com.ferelin.features.home.cryptos.CryptosRoute
import com.ferelin.features.home.favourite.FavouriteStocksRoute
import com.ferelin.features.home.stocks.StocksRoute
import com.ferelin.stockprice.components.HomeTab
import com.ferelin.stockprice.ui.viewModel.HomeStateUi
import com.ferelin.stockprice.ui.viewModel.HomeViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreenRoute(
  onSearchRoute: () -> Unit,
  onSettingsRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModel = getViewModel<HomeViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  HomeScreen(
    uiState = uiState,
    onScreenSelected = viewModel::onScreenSelected,
    onSearchClick = onSearchRoute,
    onSettingsClick = onSettingsRoute,
    onCryptosRoute = { CryptosRoute() },
    onStocksRoute = { StocksRoute(onStockRoute = onStockRoute) },
    onFavouriteStocksRoute = { FavouriteStocksRoute(onStockRoute = onStockRoute) }
  )
}

@Composable
private fun HomeScreen(
  uiState: HomeStateUi,
  onScreenSelected: (Int) -> Unit,
  onSearchClick: () -> Unit,
  onSettingsClick: () -> Unit,
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
      .background(com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(14.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(id = R.string.titleHome),
        style = com.ferelin.stockprice.theme.AppTheme.typography.title1,
        color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
      )
      ClickableIcon(
        backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
        iconTint = com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary,
        imageVector = Icons.Default.Settings,
        contentDescription = stringResource(id = R.string.descriptionSettings),
        onClick = onSettingsClick
      )
    }
    Spacer(modifier = Modifier.height(12.dp))
    TopSearchField(onClick = onSearchClick)
    Spacer(modifier = Modifier.height(12.dp))
    Tabs(
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
private fun TopSearchField(
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  SearchField(
    modifier = modifier,
    borderWidth = 1.dp,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.padding(start = 12.dp))
      Icon(
        painter = painterResource(R.drawable.ic_search_17x18),
        contentDescription = stringResource(id = R.string.descriptionImageSearch),
        tint = com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary
      )
      Spacer(modifier = Modifier.width(12.dp))
      ConstrainedText(
        text = stringResource(R.string.hintFindCompany),
        style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
        color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
      )
    }
  }
}

@Composable
private fun Tabs(
  modifier: Modifier = Modifier,
  pagerState: PagerState,
  onTabClick: (Int) -> Unit
) {
  TabRow(
    modifier = modifier,
    backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
    selectedTabIndex = pagerState.currentPage,
    indicator = { tabPositions ->
      TabRowDefaults.Indicator(
        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
      )
    }
  ) {
    repeat(TOTAL_SCREENS) { index ->
      when (index) {
        CRYPTOS_SCREEN_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleCryptos),
            isSelected = pagerState.currentPage == CRYPTOS_SCREEN_INDEX,
            onClick = { onTabClick(CRYPTOS_SCREEN_INDEX) }
          )
        }
        STOCKS_SCREEN_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleStocks),
            isSelected = pagerState.currentPage == STOCKS_SCREEN_INDEX,
            onClick = { onTabClick(STOCKS_SCREEN_INDEX) }
          )
        }
        FAVOURITE_STOCKS_SCREEN_INDEX -> {
          HomeTab(
            title = stringResource(id = R.string.titleFavourite),
            isSelected = pagerState.currentPage == FAVOURITE_STOCKS_SCREEN_INDEX,
            onClick = { onTabClick(FAVOURITE_STOCKS_SCREEN_INDEX) }
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
    count = TOTAL_SCREENS,
    state = pagerState
  ) { pageIndex ->
    when (pageIndex) {
      CRYPTOS_SCREEN_INDEX -> onCryptosRoute()
      STOCKS_SCREEN_INDEX -> onStocksRoute()
      FAVOURITE_STOCKS_SCREEN_INDEX -> onFavouriteStocksRoute()
      else -> error("There is no screen for index [$pageIndex]")
    }
  }
}

private const val TOTAL_SCREENS = 3
internal const val CRYPTOS_SCREEN_INDEX = 0
internal const val STOCKS_SCREEN_INDEX = 1
internal const val FAVOURITE_STOCKS_SCREEN_INDEX = 2