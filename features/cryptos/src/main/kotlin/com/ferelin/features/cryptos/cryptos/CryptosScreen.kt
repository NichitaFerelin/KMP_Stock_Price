package com.ferelin.features.cryptos.cryptos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.APP_CONTENT_PADDING
import com.ferelin.core.ui.APP_TOOLBAR_BASELINE
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.AppCircularProgressIndicator
import com.ferelin.core.ui.components.ScreenTitle
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.cryptos.components.CryptoItem
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@Composable
fun CryptosScreenRoute(
    onBackRoute: () -> Unit
) {
    val viewModel = getViewModel<CryptosViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    CryptosScreen(
        cryptos = uiState.cryptos,
        cryptosLce = uiState.cryptosLce,
        cryptosFetchLce = uiState.cryptosFetchLce,
        onBackClick = onBackRoute,
        onCryptosRefresh = viewModel::fetchCryptos
    )
}

@Composable
private fun CryptosScreen(
    cryptos: List<CryptoViewData>,
    cryptosLce: LceState,
    cryptosFetchLce: LceState,
    onBackClick: () -> Unit,
    onCryptosRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppTheme.colors.backgroundPrimary)
    ) {
        TopBar(onBackClick = onBackClick)

        when (cryptosLce) {
            is LceState.Content -> {
                CryptosSection(
                    cryptos = cryptos,
                    cryptosFetchLce = cryptosFetchLce,
                    onCryptosRefresh = onCryptosRefresh
                )
            }
            is LceState.Loading -> {
                Spacer(modifier = Modifier.height(12.dp))
                AppCircularProgressIndicator()
            }
            is LceState.Error -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.errorDownload),
                    style = AppTheme.typography.title2,
                    color = AppTheme.colors.textPrimary
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(APP_TOOLBAR_BASELINE / 2)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.backgroundPrimary,
                        AppTheme.colors.backgroundSecondary
                    ),
                    startY = LocalDensity.current.run { (APP_TOOLBAR_BASELINE / 4).toPx() }
                )
            )
            .padding(
                start = APP_CONTENT_PADDING,
                end = APP_CONTENT_PADDING,
                top = 10.dp
            )
    ) {
        ScreenTitle(
            title = stringResource(id = R.string.titleCryptos),
            onBackClick = onBackClick
        )
    }
}

@Composable
private fun CryptosSection(
    modifier: Modifier = Modifier,
    cryptos: List<CryptoViewData>,
    cryptosFetchLce: LceState,
    onCryptosRefresh: () -> Unit
) {
    SwipeRefresh(
        modifier = modifier.fillMaxWidth(),
        state = rememberSwipeRefreshState(
            isRefreshing = cryptosFetchLce is LceState.Loading
        ),
        onRefresh = onCryptosRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = AppTheme.colors.backgroundSecondary,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = APP_CONTENT_PADDING),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cryptos) {
                CryptoItem(
                    name = it.name,
                    logoUrl = it.logoUrl,
                    price = it.price,
                    profit = it.profit
                )
            }
        }
    }
}