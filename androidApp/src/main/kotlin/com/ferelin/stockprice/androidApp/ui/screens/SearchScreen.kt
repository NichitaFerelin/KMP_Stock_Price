@file:OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)

package com.ferelin.stockprice.androidApp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import com.ferelin.stockprice.androidApp.domain.entity.LceState
import com.ferelin.stockprice.sharedComposables.components.*
import com.ferelin.stockprice.androidApp.ui.viewData.SearchViewData
import com.ferelin.stockprice.androidApp.ui.viewData.StockViewData
import com.ferelin.stockprice.androidApp.ui.viewModel.SearchStateUi
import com.ferelin.stockprice.androidApp.ui.viewModel.SearchViewModel
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchRoute(
  onBackRoute: () -> Unit,
  onStockRoute: (StockViewData) -> Unit
) {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: SearchViewModel by remember { viewModelWrapper.viewModel() }
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
          modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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

@Composable
private fun TopSearchField(
  modifier: Modifier = Modifier,
  inputText: String,
  showCloseIcon: Boolean,
  onTextChanged: (String) -> Unit,
  onBackClick: () -> Unit
) {
  val keyboardController = LocalSoftwareKeyboardController.current

  SearchField(modifier = modifier, borderWidth = 2.dp, onClick = { /**/ }) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.padding(start = 12.dp))
      ClickableIcon(imageVector = Icons.Default.ArrowBack,
        backgroundColor = AppTheme.colors.backgroundPrimary,
        contentDescription = ""/*stringResource(R.string.descriptionBack)*/,
        iconTint = AppTheme.colors.buttonPrimary,
        onClick = {
          keyboardController?.hide()
          onBackClick.invoke()
        })
      Spacer(modifier = Modifier.width(8.dp))
      TextField(inputValue = inputText,
        placeholder = ""/*stringResource(id = R.string.hintEnterSearchRequest)*/,
        onValueChange = onTextChanged,
        keyboardActions = KeyboardActions { keyboardController?.hide() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        trailingIcon = {
          AnimatedVisibility(
            visible = showCloseIcon, enter = scaleIn(), exit = scaleOut()
          ) {
            ClickableIcon(backgroundColor = AppTheme.colors.backgroundPrimary,
              imageVector = Icons.Default.Close,
              iconTint = AppTheme.colors.buttonPrimary,
              contentDescription = ""/*stringResource(id = R.string.descriptionIconClose)*/,
              onClick = { onTextChanged("") })
          }
        })
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
    title = ""/*stringResource(R.string.titlePopularRequests)*/,
    searchRequests = popularSearchRequests,
    searchRequestsLce = popularSearchRequestsLce,
    onTickerClick = onTickerClick
  )
  Spacer(modifier = Modifier.height(16.dp))
  SearchRequests(
    title = ""/*stringResource(R.string.titleYourSearches)*/,
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
    modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
  ) {
    /*Image(
      painter = painterResource(id = R.mipmap.ic_pointing_glass),
      contentDescription = stringResource(id = R.string.descriptionNoSearchResults)
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = stringResource(id = R.string.hintNoSearchResults),
      style = AppTheme.typography.body1,
      color = AppTheme.colors.textPrimary
    )*/
  }
}