package com.ferelin.features.about.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.ui.component.ChartButton
import com.google.accompanist.insets.statusBarsPadding

@Composable
internal fun ChartRoute(chartViewModel: ChartViewModel) {
  val uiState by chartViewModel.uiState.collectAsState()

  ChartScreen(
    chartScreenStateUi = uiState,
    onChartModeSelected = chartViewModel::onChartModeSelect
  )
}

@Composable
internal fun ChartScreen(
  chartScreenStateUi: ChartScreenStateUi,
  onChartModeSelected: (ChartViewMode) -> Unit
) {
  Row(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    horizontalArrangement = Arrangement.Center
  ) {
    Spacer(modifier = Modifier.height(30.dp))
    Text(text = chartScreenStateUi.stockPrice)
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = chartScreenStateUi.stockProfit)
    Chart(chartPastPrices = chartScreenStateUi.priceHistory)
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ChartButton(
        name = stringResource(R.string.titleChartCardDays),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.Days,
        onClick = { onChartModeSelected.invoke(ChartViewMode.Days) }
      )
      ChartButton(
        name = stringResource(R.string.titleChartCardWeeks),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.Weeks,
        onClick = { onChartModeSelected.invoke(ChartViewMode.Weeks) }
      )
      ChartButton(
        name = stringResource(R.string.titleChartCardMonths),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.Months,
        onClick = { onChartModeSelected.invoke(ChartViewMode.Months) }
      )
      ChartButton(
        name = stringResource(R.string.titleChartCardHalfYear),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.SixMonths,
        onClick = { onChartModeSelected.invoke(ChartViewMode.SixMonths) }
      )
      ChartButton(
        name = stringResource(R.string.titleChartCardYear),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.Year,
        onClick = { onChartModeSelected.invoke(ChartViewMode.Year) }
      )
      ChartButton(
        name = stringResource(R.string.titleChartCardAll),
        selected = chartScreenStateUi.selectedChartMode == ChartViewMode.All,
        onClick = { onChartModeSelected.invoke(ChartViewMode.All) }
      )
    }
  }
}

@Composable
private fun Chart(chartPastPrices: ChartPastPrices) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(150.dp)
      .background(AppTheme.colors.backgroundPrimary)
  )
}
