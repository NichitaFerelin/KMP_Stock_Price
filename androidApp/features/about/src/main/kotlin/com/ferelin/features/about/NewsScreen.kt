package com.ferelin.features.about.news

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.startActivitySafety
import com.ferelin.core.ui.R
import com.ferelin.stockprice.ui.params.NewsParams
import com.ferelin.stockprice.components.NewsItem
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.stockprice.ui.viewModel.NewsStateUi
import com.ferelin.stockprice.ui.viewModel.NewsViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsRoute(params: NewsParams) {
  val viewModel = getViewModel<NewsViewModel>(
    parameters = {
      parametersOf(params.companyId, params.companyTicker)
    }
  )
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
        .background(com.ferelin.stockprice.theme.AppTheme.colors.backgroundPrimary),
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
          CircularProgressIndicator(color = com.ferelin.stockprice.theme.AppTheme.colors.contendTertiary)
        }
        is LceState.Error -> {
          Text(
            text = stringResource(id = R.string.errorDownload),
            style = com.ferelin.stockprice.theme.AppTheme.typography.body1,
            color = com.ferelin.stockprice.theme.AppTheme.colors.textPrimary
          )
        }
        else -> Unit
      }
    }
  }
}