package com.ferelin.features.stocks.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.component.SearchField
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.stocks.ui.common.component.CryptoItem
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState

@Composable
fun CommonRoute(commonViewModel: CommonViewModel) {
  val uiState by commonViewModel.uiState.collectAsState()

  CommonScreen(
    commonStateUi = uiState,
    onScreenSelected = commonViewModel::onScreenSelected,
    onSearchFieldClick = { }
  )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun CommonScreen(
  commonStateUi: CommonStateUi,
  onScreenSelected: (Int) -> Unit,
  onSearchFieldClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(4.dp))
    SearchField(
      borderWidth = 1.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .clickable(onClick = onSearchFieldClick)
      ) {
        Icon(
          painter = painterResource(R.drawable.ic_search_17x18),
          contentDescription = null,
          tint = AppTheme.colors.contendSecondary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = stringResource(R.string.hintFindCompany))
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
      items(
        items = commonStateUi.cryptos
      ) { cryptoViewData ->
        CryptoItem(
          name = cryptoViewData.name,
          iconUrl = cryptoViewData.logoUrl,
          price = cryptoViewData.price,
          profit = cryptoViewData.profit
        )
      }
    }
    Spacer(modifier = Modifier.height(8.dp))

    val pagerState = rememberPagerState(initialPage = commonStateUi.selectedScreenIndex)

    TabRow(
      selectedTabIndex = commonStateUi.selectedScreenIndex,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
      }
    ) {
      repeat(TOTAL_PAGES) { index ->
        when (index) {
          STOCKS_INDEX -> {
            Tab(
              text = {
                Text(
                  text = stringResource(R.string.titleStocks),
                  style = AppTheme.typography.caption1
                )
              },
              selected = pagerState.currentPage == index,
              onClick = { onScreenSelected(STOCKS_INDEX) },
            )
          }
          FAVOURITE_STOCKS_INDEX -> {
            Tab(
              text = {
                Text(
                  text = stringResource(R.string.titleFavourite),
                  style = AppTheme.typography.caption1
                )
              },
              selected = pagerState.currentPage == index,
              onClick = { onScreenSelected(FAVOURITE_STOCKS_INDEX) },
            )
          }
          else -> error("There is no tab for the screen index [$index] ")
        }
      }
    }
    HorizontalPager(
      count = TOTAL_PAGES,
      state = pagerState
    ) { pageIndex ->
      when (pageIndex) {
        STOCKS_INDEX -> {}
        FAVOURITE_STOCKS_INDEX -> {}
        else -> error("There is no screen for the screen index [$pageIndex]")
      }
    }
  }
}

internal const val TOTAL_PAGES = 2
internal const val STOCKS_INDEX = 0
internal const val FAVOURITE_STOCKS_INDEX = 1