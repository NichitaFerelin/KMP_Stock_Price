@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.features.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.R
import com.ferelin.core.ui.component.APP_TOP_PADDING
import com.ferelin.core.ui.component.ClickableIcon
import com.ferelin.core.ui.component.ConstrainedText
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.chart.ChartRoute
import com.ferelin.features.about.news.NewsRoute
import com.ferelin.features.about.profile.ProfileRoute
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*

@Composable
fun AboutRoute(
  deps: AboutDeps,
  params: AboutParams,
  onBackRoute: () -> Unit
) {
  val component = remember {
    DaggerAboutComponent.builder()
      .dependencies(deps)
      .params(params)
      .build()
  }
  val viewModel: AboutViewModel = viewModel(
    factory = component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  AboutScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::switchFavourite,
    onScreenSelected = viewModel::onScreenSelected,
    onBackRoute = onBackRoute,
    onProfileRoute = {
      val profileParams = remember { ProfileParams(params.companyId) }
      ProfileRoute(deps, profileParams)
    },
    onChartRoute = {
      val chartParams = remember { ChartParams(params.companyId, params.companyTicker) }
      ChartRoute(deps, chartParams)
    },
    onNewsRoute = {
      val newsParams = remember { NewsParams(params.companyId, params.companyTicker) }
      NewsRoute(deps, newsParams)
    }
  )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun AboutScreen(
  uiState: AboutStateUi,
  onFavouriteIconClick: () -> Unit,
  onScreenSelected: (Int) -> Unit,
  onBackRoute: () -> Unit,
  onProfileRoute: @Composable () -> Unit,
  onChartRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  val pagerState = rememberPagerState(initialPage = uiState.selectedScreenIndex)
  LaunchedEffect(key1 = uiState.selectedScreenIndex) {
    pagerState.animateScrollToPage(uiState.selectedScreenIndex)
  }
  BackHandler(enabled = uiState.selectedScreenIndex != PROFILE_INDEX) {
    onScreenSelected(PROFILE_INDEX)
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    TopBar(
      modifier = Modifier
        .fillMaxWidth()
        .padding(APP_TOP_PADDING),
      companyTicker = uiState.companyTicker,
      companyName = uiState.companyName,
      isFavourite = uiState.isFavourite,
      onFavouriteIconClick = onFavouriteIconClick,
      onBackClick = onBackRoute
    )
    Spacer(modifier = Modifier.height(8.dp))
    Tabs(
      modifier = Modifier.fillMaxWidth(),
      pagerState = pagerState,
      onScreenTabClicked = onScreenSelected
    )
    ScreensPager(
      pagerState = pagerState,
      onProfileRoute = onProfileRoute,
      onChartRoute = onChartRoute,
      onNewsRoute = onNewsRoute
    )
  }
}

@Composable
private fun TopBar(
  modifier: Modifier = Modifier,
  companyTicker: String,
  companyName: String,
  isFavourite: Boolean,
  onFavouriteIconClick: () -> Unit,
  onBackClick: () -> Unit
) {
  Row(
    modifier = modifier.padding(horizontal = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    ClickableIcon(
      modifier = Modifier.weight(0.2f),
      painter = painterResource(id = R.drawable.ic_arrow_back_24),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      tint = AppTheme.colors.buttonPrimary,
      contentDescription = stringResource(R.string.descriptionBack),
      onClick = onBackClick
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column(
      modifier = Modifier.weight(0.6f),
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ConstrainedText(
        text = companyTicker,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body1
      )
      ConstrainedText(
        text = companyName,
        color = AppTheme.colors.textPrimary,
        style = AppTheme.typography.body2
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    ClickableIcon(
      modifier = Modifier
        .clickable(onClick = onFavouriteIconClick)
        .weight(0.2f),
      backgroundColor = AppTheme.colors.backgroundPrimary,
      painter = painterResource(R.drawable.ic_favourite_16),
      tint = if (isFavourite) {
        AppTheme.colors.iconActive
      } else AppTheme.colors.iconDisabled,
      contentDescription = if (isFavourite) {
        stringResource(R.string.descriptionRemoveFromFavourites)
      } else stringResource(R.string.descriptionAddToFavourites),
      onClick = onFavouriteIconClick
    )
  }
}

@Composable
private fun Tabs(
  modifier: Modifier = Modifier,
  pagerState: PagerState,
  onScreenTabClicked: (Int) -> Unit
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
    repeat(TOTAL_PAGES) { index ->
      when (index) {
        PROFILE_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleProfile),
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(PROFILE_INDEX) },
          )
        }
        CHART_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleChart),
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(CHART_INDEX) },
          )
        }
        NEWS_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleNews),
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(NEWS_INDEX) },
          )
        }
        else -> error("There is no tab for the screen index [$index] ")
      }
    }
  }
}

@Composable
private fun ScreensPager(
  pagerState: PagerState,
  onProfileRoute: @Composable () -> Unit,
  onChartRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  HorizontalPager(
    count = TOTAL_PAGES,
    state = pagerState
  ) { pageIndex ->
    when (pageIndex) {
      PROFILE_INDEX -> onProfileRoute()
      CHART_INDEX -> onChartRoute()
      NEWS_INDEX -> onNewsRoute()
      else -> error("There is no screen for the screen index [$pageIndex]")
    }
  }
}

private const val TOTAL_PAGES = 3
internal const val PROFILE_INDEX = 0
internal const val CHART_INDEX = 1
internal const val NEWS_INDEX = 2