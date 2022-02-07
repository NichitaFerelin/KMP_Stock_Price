package com.ferelin.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.core.ui.viewData.StockViewData
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StocksList(
  stocks: List<StockViewData>,
  stocksLce: LceState,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onStockClick: (StockViewData) -> Unit
) {
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var fabIsVisible by remember { mutableStateOf(false) }
  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        fabIsVisible = available.y < 0
        return Offset.Zero
      }
    }
  }

  Box(
    Modifier
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    contentAlignment = Alignment.Center
  ) {
    Crossfade(targetState = stocksLce) { lce ->
      when (lce) {
        is LceState.Content -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .nestedScroll(nestedScrollConnection),
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
                index = stockViewData.id.value,
                iconUrl = stockViewData.logoUrl,
                ticker = stockViewData.ticker,
                name = stockViewData.name,
                isFavourite = stockViewData.isFavourite,
                onFavouriteIconClick = { onFavouriteIconClick(stockViewData) },
                onClick = { onStockClick(stockViewData) }
              )
            }
          }
          AnimatedVisibility(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp),
            visible = fabIsVisible,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            FloatingActionButton(
              backgroundColor = AppTheme.colors.buttonSecondary,
              onClick = {
                fabIsVisible = false
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
          }
        }
        is LceState.Loading -> {
          CircularProgressIndicator(color = AppTheme.colors.contendTertiary)
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