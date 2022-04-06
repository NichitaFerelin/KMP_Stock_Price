@file:OptIn(ExperimentalPagerApi::class)

package com.ferelin.features.about.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.ViewModelWrapper
import com.ferelin.features.about.news.NewsRoute
import com.ferelin.features.about.profile.ProfileRoute
import com.ferelin.stockprice.components.ClickableIcon
import com.ferelin.stockprice.components.ConstrainedText
import com.ferelin.stockprice.theme.AppTheme
import com.ferelin.stockprice.ui.params.AboutParams
import com.ferelin.stockprice.ui.params.NewsParams
import com.ferelin.stockprice.ui.params.ProfileParams
import com.ferelin.stockprice.ui.viewModel.AboutStateUi
import com.ferelin.stockprice.ui.viewModel.AboutViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import org.koin.androidx.compose.getViewModel

@Composable
fun AboutRoute(
  params: AboutParams,
  onBackRoute: () -> Unit
) {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: AboutViewModel by remember {
    viewModelWrapper.viewModel(
      params.companyId,
      params.companyName,
      params.companyTicker
    )
  }

  /*viewModel<AboutViewModel>(
      parameters = {
        parametersOf(

        )
      }
    )*/
  /* val viewModelWrapper = getViewModel<AboutModule>(
     parameters = {

     }
   )*/
  val uiState by viewModelWrapper.viewModel.uiState.collectAsState()

  AboutScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModelWrapper.viewModel::switchFavourite,
    onScreenSelected = viewModelWrapper.viewModel::onScreenSelected,
    onBackRoute = onBackRoute,
    onProfileRoute = {
      val profileParams = remember { ProfileParams(params.companyId) }
      ProfileRoute(profileParams)
    },
    onNewsRoute = {
      val newsParams = remember { NewsParams(params.companyId, params.companyTicker) }
      NewsRoute(newsParams)
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
        .fillMaxWidth(),
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
      backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
      iconTint = com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary,
      contentDescription = stringResource(R.string.descriptionBack),
      onClick = onBackClick
    )
    Column(
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ConstrainedText(
        text = companyTicker,
        color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary,
        style = com.ferelin.stockprice.theme.AppTheme.typography.body1
      )
      ConstrainedText(
        text = companyName,
        color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary,
        style = com.ferelin.stockprice.theme.AppTheme.typography.body2
      )
    }
    ClickableIcon(
      backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
      painter = painterResource(R.drawable.ic_favourite_16),
      iconTint = if (isFavourite) com.ferelin.stockprice.theme.AppTheme.colors.iconActive else com.ferelin.stockprice.theme.AppTheme.colors.iconDisabled,
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
    backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary,
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
                style = com.ferelin.stockprice.theme.AppTheme.typography.title2,
                color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
              )
            },
            selected = pagerState.currentPage == index,
            onClick = { onScreenTabClicked.invoke(PROFILE_INDEX) },
          )
        }
        NEWS_INDEX -> {
          Tab(
            text = {
              Text(
                text = stringResource(R.string.titleNews),
                style = com.ferelin.stockprice.theme.AppTheme.typography.title2,
                color = com.ferelin.stockprice.theme.AppTheme.colors.textTertiary
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
  onNewsRoute: @Composable () -> Unit
) {
  HorizontalPager(
    count = TOTAL_PAGES,
    state = pagerState
  ) { pageIndex ->
    when (pageIndex) {
      PROFILE_INDEX -> onProfileRoute()
      NEWS_INDEX -> onNewsRoute()
      else -> error("There is no screen for index [$pageIndex]")
    }
  }
}