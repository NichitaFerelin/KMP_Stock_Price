package com.ferelin.stockprice.sharedComposables.components

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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import kotlinx.coroutines.launch

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

    Crossfade(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.backgroundPrimary),
        targetState = stocksLce
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
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Scroll up",
                                tint = AppTheme.colors.buttonPrimary
                            )
                        }
                    }
                }
                is LceState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.contendTertiary
                    )
                }
                is LceState.Error -> {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Error",
                        style = AppTheme.typography.body1,
                        color = AppTheme.colors.textPrimary
                    )
                }
                else -> Unit
            }
        }
    }
}