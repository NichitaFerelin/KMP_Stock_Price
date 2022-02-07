package com.ferelin.features.about.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.features.about.uiComponents.NewsItem

@Composable
fun NewsRoute(deps: NewsDeps, params: NewsParams) {
  val componentViewModel = viewModel<NewsComponentViewModel>(
    factory = NewsComponentViewModelFactory(deps, params)
  )
  val viewModel = viewModel<NewsViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
  val uiState by viewModel.uiState.collectAsState()

  NewsScreen(
    uiState = uiState,
    onUrlClick = { }
  )
}

@Composable
private fun NewsScreen(
  uiState: NewsStateUi,
  onUrlClick: (String) -> Unit
) {
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