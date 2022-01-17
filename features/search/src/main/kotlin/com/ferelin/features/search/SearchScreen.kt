@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.ferelin.features.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.R
import com.ferelin.core.ui.component.ClickableIcon
import com.ferelin.core.ui.component.SearchField
import com.ferelin.core.ui.component.StocksList
import com.ferelin.core.ui.component.TextField
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.features.search.component.SearchRequests
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SearchRoute(
  deps: SearchDeps,
  onBackRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val component = remember {
    DaggerSearchComponent.builder()
      .dependencies(deps)
      .build()
  }
  val viewModel = viewModel<SearchViewModel>(
    factory = component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  SearchScreen(
    uiState = uiState,
    onSearchTextChanged = viewModel::onSearchTextChanged,
    onTickerClick = viewModel::onTickerClick,
    onStockClick = onStockRoute,
    onFavouriteIconClick = viewModel::onFavouriteIconClick,
    onBackClick = onBackRoute
  )
}

@Composable
private fun SearchScreen(
  uiState: SearchStateUi,
  onSearchTextChanged: (String) -> Unit,
  onTickerClick: (SearchViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onBackClick: () -> Unit,
) {
  BackHandler(enabled = uiState.inputSearchRequest.isNotEmpty()) {
    onSearchTextChanged("")
  }

  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary)
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    TopSearchField(
      inputText = uiState.inputSearchRequest,
      showCloseIcon = uiState.showCloseIcon,
      onTextChanged = onSearchTextChanged,
      onBackClick = onBackClick
    )

    when {
      uiState.searchResultsLce is LceState.Loading -> {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
        }
      }
      uiState.inputSearchRequest.isNotEmpty() && uiState.searchResults.isEmpty() -> {
        NoSearchResultsSection()
      }
      uiState.inputSearchRequest.isEmpty() -> {
        SearchRequestsSection(
          searchRequests = uiState.searchRequests,
          searchRequestsLce = uiState.searchRequestsLce,
          popularSearchRequests = uiState.popularSearchRequests,
          popularSearchRequestsLce = uiState.popularSearchRequestsLce,
          onTickerClick = onTickerClick
        )
      }
      uiState.searchResults.isNotEmpty() -> {
        SearchResultsSection(
          searchResults = uiState.searchResults,
          onFavouriteIconClick = onFavouriteIconClick,
          onStockClick = onStockClick
        )
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopSearchField(
  modifier: Modifier = Modifier,
  inputText: String,
  showCloseIcon: Boolean,
  onTextChanged: (String) -> Unit,
  onBackClick: () -> Unit
) {
  val keyboardController = LocalSoftwareKeyboardController.current

  SearchField(
    modifier = modifier,
    borderWidth = 2.dp,
    onClick = { /**/ }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.padding(start = 12.dp))
      ClickableIcon(
        painter = painterResource(R.drawable.ic_arrow_back_24),
        backgroundColor = AppTheme.colors.backgroundPrimary,
        contentDescription = stringResource(R.string.descriptionBack),
        tint = AppTheme.colors.buttonPrimary,
        onClick = onBackClick
      )
      Spacer(modifier = Modifier.width(8.dp))
      TextField(
        inputValue = inputText,
        placeholder = stringResource(id = R.string.hintEnterSearchRequest),
        onValueChange = onTextChanged,
        keyboardActions = KeyboardActions { keyboardController?.hide() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        trailingIcon = {
          AnimatedVisibility(
            visible = showCloseIcon,
            enter = scaleIn(),
            exit = scaleOut()
          ) {
            ClickableIcon(
              backgroundColor = AppTheme.colors.backgroundPrimary,
              painter = painterResource(id = R.drawable.ic_close_24),
              tint = AppTheme.colors.buttonPrimary,
              contentDescription = stringResource(id = R.string.descriptionIconClose),
              onClick = { onTextChanged("") }
            )
          }
        }
      )
    }
  }
}

@Composable
private fun SearchRequestsSection(
  searchRequests: List<SearchViewData>,
  searchRequestsLce: LceState,
  popularSearchRequests: List<SearchViewData>,
  popularSearchRequestsLce: LceState,
  onTickerClick: (SearchViewData) -> Unit
) {
  Spacer(modifier = Modifier.height(16.dp))
  SearchRequests(
    title = stringResource(R.string.titlePopularRequests),
    searchRequests = popularSearchRequests,
    searchRequestsLce = popularSearchRequestsLce,
    onTickerClick = onTickerClick
  )
  Spacer(modifier = Modifier.height(16.dp))
  SearchRequests(
    title = stringResource(R.string.titleYourSearches),
    searchRequests = searchRequests,
    searchRequestsLce = searchRequestsLce,
    onTickerClick = onTickerClick
  )
}

@Composable
private fun SearchResultsSection(
  searchResults: List<StockViewData>,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
  Spacer(modifier = Modifier.height(8.dp))
  StocksList(
    stocks = searchResults,
    stocksLce = LceState.Content,
    onFavouriteIconClick = onFavouriteIconClick,
    onStockClick = onStockClick
  )
}

@Composable
private fun NoSearchResultsSection() {
  Spacer(modifier = Modifier.height(30.dp))
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(id = R.mipmap.ic_pointing_glass),
      contentDescription = stringResource(id = R.string.descriptionNoSearchResults)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = stringResource(id = R.string.hintNoSearchResults),
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textPrimary
    )
  }
}