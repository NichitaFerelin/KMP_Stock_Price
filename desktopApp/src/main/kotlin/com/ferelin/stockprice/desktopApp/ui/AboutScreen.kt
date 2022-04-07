package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.desktopApp.ui.components.NavigationBarItem
import com.ferelin.stockprice.shared.ui.params.AboutParams
import com.ferelin.stockprice.shared.ui.params.NewsParams
import com.ferelin.stockprice.shared.ui.params.ProfileParams
import com.ferelin.stockprice.shared.ui.viewModel.*
import com.ferelin.stockprice.sharedComposables.components.ClickableIcon
import com.ferelin.stockprice.sharedComposables.components.ConstrainedText
import com.ferelin.stockprice.sharedComposables.theme.AppTheme

@Composable
fun AboutScreenRoute(
  params: AboutParams,
  onBackRoute: () -> Unit
) {
  val viewModelScope = rememberCoroutineScope()
  val viewModel: AboutViewModel = remember {
    ViewModelWrapper().viewModel(viewModelScope, params)
  }
  val uiState by viewModel.uiState.collectAsState()

  AboutScreen(
    uiState = uiState,
    onFavouriteIconClick = viewModel::switchFavourite,
    onScreenSelected = viewModel::onScreenSelected,
    onBackClick = onBackRoute,
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
  onBackClick: () -> Unit,
  onProfileRoute: @Composable () -> Unit,
  onNewsRoute: @Composable () -> Unit
) {
  Row {
    NavigationBar(
      modifier = Modifier.padding(top = APP_TOP_PADDING),
      selectedScreenIndex = uiState.selectedScreenIndex,
      onScreenSelected = onScreenSelected,
      onBackClick = onBackClick
    )
    Column {
      TopBar(
        modifier = Modifier.fillMaxWidth(),
        companyTicker = uiState.companyTicker,
        companyName = uiState.companyName,
        isFavourite = uiState.isFavourite,
        onFavouriteIconClick = onFavouriteIconClick,
      )
      Spacer(modifier = Modifier.height(6.dp))

      when (uiState.selectedScreenIndex) {
        SCREEN_PROFILE_INDEX -> onProfileRoute()
        SCREEN_NEWS_INDEX -> onNewsRoute()
        else -> throw IllegalStateException("No screen for index [$uiState.selectedScreenIndex]")
      }
    }
  }
}

@Composable
private fun NavigationBar(
  modifier: Modifier = Modifier,
  selectedScreenIndex: Int,
  onScreenSelected: (Int) -> Unit,
  onBackClick: () -> Unit
) {
  Column(
    modifier = modifier
  ) {
    Text(
      modifier = Modifier.padding(start = APP_START_PADDING),
      text = "Profile",
      style = AppTheme.typography.title1,
      color = AppTheme.colors.textPrimary
    )
    Spacer(modifier = Modifier.height(24.dp))

    Row(
      modifier = Modifier
        .width(APP_NAV_ITEM_WIDTH)
        .height(APP_NAV_ITEM_HEIGHT)
        .clickable(onClick = onBackClick)
        .padding(start = APP_START_PADDING),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "",
        tint = AppTheme.colors.buttonPrimary
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Back",
        style = AppTheme.typography.title2,
        color = AppTheme.colors.textPrimary
      )
    }

    repeat(ABOUT_TOTAL_SCREENS) { screenIndex ->
      when (screenIndex) {
        SCREEN_PROFILE_INDEX -> {
          NavigationBarItem(
            title = "Company info",
            isSelected = selectedScreenIndex == SCREEN_PROFILE_INDEX,
            onClick = { onScreenSelected(SCREEN_PROFILE_INDEX) }
          )
        }
        SCREEN_NEWS_INDEX -> {
          NavigationBarItem(
            title = "Company news",
            isSelected = selectedScreenIndex == SCREEN_NEWS_INDEX,
            onClick = { onScreenSelected(SCREEN_NEWS_INDEX) }
          )
        }
        else -> throw IllegalStateException("No screen for index [$screenIndex]")
      }
    }
  }
}

@Composable
private fun TopBar(
  modifier: Modifier = Modifier,
  companyTicker: String,
  companyName: String,
  isFavourite: Boolean,
  onFavouriteIconClick: () -> Unit,
) {
  Row(
    modifier = modifier.padding(horizontal = 50.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Icon(
      imageVector = Icons.Default.Info,
      tint = AppTheme.colors.buttonPrimary,
      contentDescription = "", /*stringResource(R.string.descriptionBack)*/
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
      iconTint = if (isFavourite) AppTheme.colors.iconActive else AppTheme.colors.iconDisabled,
      contentDescription = ""/*if (isFavourite) {
        stringResource(R.string.descriptionRemoveFromFavourites)
      } else stringResource(R.string.descriptionAddToFavourites)*/,
      onClick = onFavouriteIconClick
    )
  }
}