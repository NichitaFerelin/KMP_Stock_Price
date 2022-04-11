package com.ferelin.stockprice.androidApp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.stockprice.androidApp.R
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.androidApp.ui.startActivitySafety
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.ui.params.NewsParams
import com.ferelin.stockprice.shared.ui.viewModel.NewsStateUi
import com.ferelin.stockprice.shared.ui.viewModel.NewsViewModel
import com.ferelin.stockprice.sharedComposables.components.NewsItem
import com.ferelin.stockprice.sharedComposables.theme.AppTheme
import org.koin.androidx.compose.getViewModel

@Composable
internal fun NewsScreenRoute(params: NewsParams) {
    val viewModelWrapper = getViewModel<ViewModelWrapper>()
    val viewModel: NewsViewModel = remember { viewModelWrapper.viewModel(params) }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    NewsScreen(
        uiState = uiState,
        onUrlClick = { url ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivitySafety(intent)
        }
    )
}

@Composable
private fun NewsScreen(
    uiState: NewsStateUi,
    onUrlClick: (String) -> Unit
) {
    Crossfade(targetState = uiState.newsLce) { lceState ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.backgroundPrimary),
            contentAlignment = Alignment.Center
        ) {
            when (lceState) {
                is LceState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            start = 6.dp,
                            end = 6.dp,
                            top = 12.dp,
                            bottom = 70.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = uiState.news) { newsViewData ->
                            NewsItem(
                                source = newsViewData.source,
                                url = newsViewData.sourceUrl,
                                date = newsViewData.date,
                                title = newsViewData.headline,
                                content = newsViewData.summary,
                                onUrlClick = onUrlClick
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