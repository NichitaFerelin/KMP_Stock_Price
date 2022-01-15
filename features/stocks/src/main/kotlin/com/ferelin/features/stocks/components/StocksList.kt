package com.ferelin.features.stocks.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.component.StockItem
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.features.stocks.R
import kotlinx.coroutines.launch

@Composable
internal fun StocksList(
  stocks: List<StockViewData>,
  stocksLce: LceState,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    backgroundColor = AppTheme.colors.backgroundPrimary,
    floatingActionButton = {
      FloatingActionButton(
        backgroundColor = AppTheme.colors.buttonSecondary,
        onClick = {
          coroutineScope.launch {
            listState.animateScrollToItem(0)
          }
        }
      ) {
        Icon(
          painter = painterResource(id = R.drawable.ic_arrow_up_24),
          contentDescription = stringResource(id = R.string.descriptionScrollToTop),
          tint = AppTheme.colors.buttonPrimary
        )
      }
    },
    floatingActionButtonPosition = FabPosition.End
  ) { contentPadding ->
    when (stocksLce) {
      is LceState.Content -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
          contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = 70.dp
          ),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          state = listState
        ) {
          items(
            items = stocks
          ) { stockViewData ->
            StockItem(
              backgroundColor = if (stockViewData.id.value % 2 == 0) {
                AppTheme.colors.contendPrimary
              } else AppTheme.colors.contendSecondary,
              iconUrl = stockViewData.logoUrl,
              ticker = stockViewData.ticker,
              name = stockViewData.name,
              isFavourite = stockViewData.isFavourite,
              onFavouriteIconClick = { onFavouriteIconClick(stockViewData) },
              onClick = { onStockClick(stockViewData) }
            )
          }
        }
      }
      else -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
          contentAlignment = Alignment.Center
        ) {
          when (stocksLce) {
            is LceState.Loading -> {
              CircularProgressIndicator(
                color = AppTheme.colors.contendTertiary
              )
            }
            is LceState.Error -> {
              Text(
                text = stringResource(id = R.string.errorDownload),
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textPrimary
              )
            }
            else -> Unit
          }
        }
      }
    }
  }
}