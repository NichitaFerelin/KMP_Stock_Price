@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.stockprice.androidApp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.androidApp.R
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.params.AboutParams
import com.ferelin.stockprice.shared.ui.params.NewsParams
import com.ferelin.stockprice.shared.ui.params.ProfileParams
import com.ferelin.stockprice.shared.ui.viewModel.*
import com.ferelin.stockprice.sharedComposables.components.ClickableIcon
import com.ferelin.stockprice.sharedComposables.components.ConstrainedText
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import org.koin.androidx.compose.getViewModel

@Composable
internal fun AboutScreenRoute(
  params: AboutParams,
  onBackRoute: () -> Unit
) {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: AboutViewModel = remember { viewModelWrapper.viewModel(params) }
  val uiState by viewModel.uiState.collectAsState()

  AboutScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::switchFavourite,
    onScreenSelected = viewModel::onScreenSelected,
    onBackRoute = onBackRoute,
    onProfileRoute = {
      val profileParams = remember { ProfileParams(params.companyId) }
      ProfileScreenRoute(profileParams)
    },
    onNewsRoute = {
      val newsParams = remember { NewsParams(params.companyId, params.companyTicker) }
      NewsScreenRoute(newsParams)
    }
  )
}

@Composable
private fun AboutScreen(
  uiState: AboutStateUi,
  onFavouriteIconClick: () -> Unit,
  onScreenSelected: (Int) -> Unit,
  onBackRoute: () -> Unit,
  onProfileRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  val pagerState = rememberPagerState(initialPage = uiState.selectedScreenIndex)
  LaunchedEffect(key1 = uiState.selectedScreenIndex) {
    pagerState.animateScrollToPage(uiState.selectedScreenIndex)
  }
  BackHandler(enabled = uiState.selectedScreenIndex != SCREEN_PROFILE_INDEX) {
    onScreenSelected(SCREEN_PROFILE_INDEX)
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    TopBar(
      modifier = Modifier
        .fillMaxWidth(),
      companyTicker = uiState.companyTicker,
      companyName = uiState.companyName,
      isFavourite = uiState.isFavourite,
      onFavouriteIconClick = onFavouriteIconClick,
      onBackClick = onBackRoute
    )
    Spacer(modifier = Modifier.height(8.dp))
    PagerTabs(
      modifier = Modifier.fillMaxWidth(),
      pagerState = pagerState,
      onScreenTabClicked = onScreenSelected
    )
    ScreensPager(
      pagerState = pagerState,
      onProfileRoute = onProfileRoute,
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
      imageVector = Icons.Default.ArrowBack,
      backgroundColor = AppTheme.colors.backgroundPrimary,
      iconTint = AppTheme.colors.buttonPrimary,
      contentDescription = stringResource(R.string.descriptionBack),
      onClick = onBackClick
    )
    Column(
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
    ClickableIcon(
      backgroundColor = AppTheme.colors.backgroundPrimary,
      imageVector = Icons.Default.Star,
      iconTint = if (isFavourite) {
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
private fun PagerTabs(
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
    repeat(ABOUT_TOTAL_SCREENS) { index ->
      when (index) {
        SCREEN_PROFILE_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleProfile),
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(SCREEN_PROFILE_INDEX) },
          )
        }
        SCREEN_NEWS_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleNews),
                style = AppTheme.typography.title2,
                color = AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(SCREEN_NEWS_INDEX) },
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
  onNewsRoute: @Composable () -> Unit
) {
  HorizontalPager(
    count = ABOUT_TOTAL_SCREENS,
    state = pagerState
  ) { pageIndex ->
    when (pageIndex) {
      SCREEN_PROFILE_INDEX -> onProfileRoute()
      SCREEN_NEWS_INDEX -> onNewsRoute()
      else -> error("There is no screen for index [$pageIndex]")
    }
  }
}