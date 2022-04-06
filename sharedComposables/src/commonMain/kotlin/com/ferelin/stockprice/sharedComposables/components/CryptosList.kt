package com.ferelin.stockprice.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.components.CryptoItem
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.stockprice.ui.viewData.CryptoViewData
import kotlinx.coroutines.launch

@Composable
fun CryptosList(
  cryptos: List<CryptoViewData>,
  cryptosLce: LceState
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

  Crossfade(
    modifier = Modifier
      .fillMaxSize()
      .background(com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary),
    targetState = cryptosLce
  ) { lce ->
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
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
              items = cryptos
            ) { cryptoViewData ->
              CryptoItem(
                name = cryptoViewData.name,
                iconUrl = cryptoViewData.logoUrl,
                price = cryptoViewData.price,
                profit = cryptoViewData.profit
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
              backgroundColor = com.ferelin.stockprice.theme.AppTheme.colors.buttonSecondary,
              onClick = {
                fabIsVisible = false
                coroutineScope.launch {
                  listState.animateScrollToItem(0)
                }
              }
            ) {
              /*Icon(
                painter = painterResource(id = R.drawable.ic_arrow_up_24),
                contentDescription = stringResource(id = R.string.descriptionScrollToTop),
                tint = com.ferelin.stockprice.theme.AppTheme.colors.buttonPrimary
              )*/
            }
          }
        }
        is LceState.Loading -> {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = com.ferelin.stockprice.theme.AppTheme.colors.contendTertiary
          )
        }
        is LceState.Error -> {
          Text(
            modifier = Modifier.align(Alignment.Center),
            text = "temp"/*stringResource(id = R.string.errorDownload)*/,
            style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
            color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
          )
        }
        else -> Unit
      }
    }
  }
}