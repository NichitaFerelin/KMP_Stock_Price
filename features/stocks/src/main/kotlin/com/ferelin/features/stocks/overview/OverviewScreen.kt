@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.features.stocks.overview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ClickableIcon
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.components.SearchField
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.features.stocks.defaults.DefaultStocksRoute
import com.ferelin.features.stocks.favourites.FavouriteStocksRoute
import com.ferelin.features.stocks.uiComponents.CRYPTO_HEIGHT
import com.ferelin.features.stocks.uiComponents.CryptoItem
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun OverviewRoute(
  deps: OverviewDeps,
  onSettingsRoute: () -> Unit,
  onSearchRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val componentViewModel = viewModel<OverviewComponentViewModel>(
    factory = OverviewComponentViewModelFactory(deps)
  )
  val viewModel = viewModel<OverviewViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  OverviewScreen(
    uiState = uiState,
    onScreenSelected = viewModel::onScreenSelected,
    onSearchFieldClick = onSearchRoute,
    onSettingsClick = onSettingsRoute,
    onDefaultStocksRoute = { DefaultStocksRoute(deps, onStockRoute) },
    onFavouriteStockRoute = { FavouriteStocksRoute(deps = deps) }
  )
}

@Composable
private fun OverviewScreen(
  uiState: OverviewStateUi,
  onScreenSelected: (Int) -> Unit,
  onSearchFieldClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onDefaultStocksRoute: @Composable () -> Unit,
  onFavouriteStockRoute: @Composable () -> Unit,
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
  BackHandler(enabled = uiState.selectedScreenIndex == FAVOURITE_STOCKS_INDEX) {
    onScreenSelected(DEFAULT_STOCKS_INDEX)
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    TopSearchField(onClick = onSearchFieldClick)
    Spacer(modifier = Modifier.height(12.dp))
    Cryptos(
      cryptos = uiState.cryptos,
      cryptosLce = uiState.cryptosLce
    )
    Spacer(modifier = Modifier.height(16.dp))
    Titles(
      selectedScreenIndex = uiState.selectedScreenIndex,
      onScreenSelected = onScreenSelected,
      onSettingsClick = onSettingsClick
    )
    HorizontalPager(
      count = TOTAL_PAGES,
      state = pagerState
    ) { pageIndex ->
      when (pageIndex) {
        DEFAULT_STOCKS_INDEX -> onDefaultStocksRoute()
        FAVOURITE_STOCKS_INDEX -> onFavouriteStockRoute()
        else -> error("There is no screen for the screen index [$pageIndex]")
      }
    }
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
        tint = AppTheme.colors.buttonPrimary
      )
      Spacer(modifier = Modifier.width(12.dp))
      ConstrainedText(
        text = stringResource(R.string.hintFindCompany),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.textPrimary
      )
    }
  }
}

@Composable
private fun Cryptos(
  modifier: Modifier = Modifier,
  cryptos: List<CryptoViewData>,
  cryptosLce: LceState
) {
  Crossfade(targetState = cryptosLce) { lce ->
    when (lce) {
      is LceState.Content -> {
        LazyRow(
          modifier = modifier.fillMaxWidth(),
          contentPadding = PaddingValues(horizontal = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(
            items = cryptos
          ) { viewData ->
            CryptoItem(
              name = viewData.name,
              iconUrl = viewData.logoUrl,
              price = viewData.price,
              profit = viewData.profit
            )
          }
        }
      }
      is LceState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(CRYPTO_HEIGHT),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
        }
      }
      is LceState.Error -> {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(CRYPTO_HEIGHT),
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

@Composable
private fun Titles(
  modifier: Modifier = Modifier,
  selectedScreenIndex: Int,
  onScreenSelected: (Int) -> Unit,
  onSettingsClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val selectedTextStyle = AppTheme.typography.title1
  val selectedTextColor = AppTheme.colors.textPrimary
  val defaultTextStyle = AppTheme.typography.title2
  val defaultTextColor = AppTheme.colors.textTertiary

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        modifier = Modifier
          .clickable(
            interactionSource = interactionSource,
            indication = null
          ) { onScreenSelected(DEFAULT_STOCKS_INDEX) },
        text = stringResource(id = R.string.titleStocks),
        style = if (selectedScreenIndex == DEFAULT_STOCKS_INDEX) {
          selectedTextStyle
        } else defaultTextStyle,
        color = if (selectedScreenIndex == DEFAULT_STOCKS_INDEX) {
          selectedTextColor
        } else defaultTextColor
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        modifier = Modifier
          .clickable(
            interactionSource = interactionSource,
            indication = null
          ) { onScreenSelected(FAVOURITE_STOCKS_INDEX) },
        text = stringResource(id = R.string.titleFavourite),
        style = if (selectedScreenIndex == FAVOURITE_STOCKS_INDEX) {
          selectedTextStyle
        } else defaultTextStyle,
        color = if (selectedScreenIndex == FAVOURITE_STOCKS_INDEX) {
          selectedTextColor
        } else defaultTextColor
      )
    }
    ClickableIcon(
      modifier = Modifier.align(Alignment.CenterEnd),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      iconTint = AppTheme.colors.buttonPrimary,
      imageVector = Icons.Default.Settings,
      contentDescription = stringResource(id = R.string.descriptionSettings),
      onClick = onSettingsClick
    )
  }
}

private const val TOTAL_PAGES = 2
internal const val DEFAULT_STOCKS_INDEX = 0
internal const val FAVOURITE_STOCKS_INDEX = 1