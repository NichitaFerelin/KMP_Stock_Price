package com.ferelin.features.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.R
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.chart.ChartRoute
import com.ferelin.features.about.news.NewsRoute
import com.ferelin.features.about.profile.ProfileRoute
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState

@Composable
fun AboutRoute(aboutDeps: AboutDeps, aboutParams: AboutParams) {
  val aboutComponent = DaggerAboutComponent.builder()
    .dependencies(aboutDeps)
    .params(aboutParams)
    .build()

  val aboutViewModel: AboutViewModel = viewModel(
    factory = aboutComponent.viewModelFactory()
  )
  val uiState by aboutViewModel.uiState.collectAsState()

  AboutScreen(
    aboutStateUi = uiState,
    onFavouriteIconClick = aboutViewModel::switchFavourite,
    onScreenTabClicked = aboutViewModel::onScreenSelected,
    onProfileRoute = {
      val params = remember { ProfileParams(aboutParams.companyId) }
      ProfileRoute(deps = aboutDeps, params = params)
    },
    onChartRoute = {
      val params = remember { ChartParams(aboutParams.companyId, aboutParams.companyTicker) }
      ChartRoute(chartDeps = aboutDeps, chartParams = params)
    },
    onNewsRoute = {
      val params = remember { NewsParams(aboutParams.companyId, aboutParams.companyTicker) }
      NewsRoute(deps = aboutDeps, params = params)
    }
  )
}

@Composable
internal fun AboutScreen(
  aboutStateUi: AboutStateUi,
  onFavouriteIconClick: () -> Unit,
  onScreenTabClicked: (Int) -> Unit,
  onProfileRoute: @Composable () -> Unit,
  onChartRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    TopBar(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 9.dp, vertical = 4.dp),
      companyTicker = aboutStateUi.companyTicker,
      companyName = aboutStateUi.companyName,
      isFavourite = aboutStateUi.isFavourite,
      onFavouriteIconClick = onFavouriteIconClick
    )
    ScreensPager(
      selectedScreenIndex = aboutStateUi.selectedScreenIndex,
      onScreenTabClicked = onScreenTabClicked,
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
  onFavouriteIconClick: () -> Unit
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Icon(
      painter = painterResource(id = R.drawable.ic_arrow_back_24),
      tint = AppTheme.colors.contendPrimary,
      contentDescription = stringResource(R.string.descriptionBack)
    )
    Column(
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = companyTicker,
        style = AppTheme.typography.largeTitle,
        color = AppTheme.colors.textPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = companyName,
        style = AppTheme.typography.subtitle,
        color = AppTheme.colors.textPrimary
      )
    }
    Icon(
      modifier = Modifier.clickable(onClick = onFavouriteIconClick),
      painter = painterResource(R.drawable.ic_favourite_16),
      tint = if (isFavourite) AppTheme.colors.contendSecondary else AppTheme.colors.contendPrimary,
      contentDescription = if (isFavourite) {
        stringResource(R.string.descriptionRemoveFromFavourites)
      } else stringResource(R.string.descriptionAddToFavourites)
    )
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScreensPager(
  selectedScreenIndex: Int,
  onScreenTabClicked: (Int) -> Unit,
  onProfileRoute: @Composable () -> Unit,
  onChartRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  val pagerState = rememberPagerState(initialPage = selectedScreenIndex)

  TabRow(
    selectedTabIndex = selectedScreenIndex,
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
                style = AppTheme.typography.caption1
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
                style = AppTheme.typography.caption1
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
                style = AppTheme.typography.caption1
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
  HorizontalPager(
    count = 3,
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

internal const val TOTAL_PAGES = 3
internal const val PROFILE_INDEX = 0
internal const val CHART_INDEX = 1
internal const val NEWS_INDEX = 2