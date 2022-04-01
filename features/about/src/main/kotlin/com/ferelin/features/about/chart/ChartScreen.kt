package com.ferelin.features.about.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferelin.core.ui.R
import com.ferelin.core.ui.components.ConstrainedText
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.theme.AppTheme
import com.ferelin.features.about.uiComponents.ChartButton
import com.google.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChartRoute(params: ChartParams) {
  val viewModel = getViewModel<ChartViewModel>(
    parameters = {
      parametersOf(params.companyId, params.companyTicker)
    }
  )
  val uiState by viewModel.uiState.collectAsState()

  ChartScreen(
    uiState = uiState,
    onChartModeSelected = viewModel::onChartModeSelected
  )
}

@Composable
private fun ChartScreen(
  uiState: ChartScreenStateUi,
  onChartModeSelected: (ChartViewMode) -> Unit
) {
  Column(
    modifier = Modifier
      .statusBarsPadding()
      .fillMaxSize()
      .background(AppTheme.colors.backgroundPrimary),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(30.dp))
    PriceField(
      price = uiState.stockPrice,
      profit = uiState.stockProfit
    )
    Spacer(modifier = Modifier.height(16.dp))
    Chart(candles = uiState.priceHistory)
    Spacer(modifier = Modifier.height(16.dp))
    ChartControlButtons(
      modifier = Modifier.fillMaxWidth(),
      chartMode = uiState.selectedChartMode,
      onChartModeSelected = onChartModeSelected
    )
  }
}

@Composable
private fun ColumnScope.PriceField(
  price: String,
  profit: String
) {
  ConstrainedText(
    modifier = Modifier.padding(horizontal = 12.dp),
    text = price,
    style = AppTheme.typography.title1,
    color = AppTheme.colors.textPrimary
  )
  Spacer(modifier = Modifier.height(6.dp))
  ConstrainedText(
    modifier = Modifier.padding(horizontal = 12.dp),
    text = profit,
    style = AppTheme.typography.body2,
    color = AppTheme.colors.textPrimary
  )
}

@Composable
private fun Chart(candles: Candles) {
  /**/
}

@Composable
private fun ChartControlButtons(
  modifier: Modifier,
  chartMode: ChartViewMode,
  onChartModeSelected: (ChartViewMode) -> Unit
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceAround,
  ) {
    ChartButton(
      text = stringResource(R.string.titleChartCardDays),
      selected = chartMode == ChartViewMode.Days,
      onClick = { onChartModeSelected.invoke(ChartViewMode.Days) }
    )
    ChartButton(
      text = stringResource(R.string.titleChartCardWeeks),
      selected = chartMode == ChartViewMode.Weeks,
      onClick = { onChartModeSelected.invoke(ChartViewMode.Weeks) }
    )
    ChartButton(
      text = stringResource(R.string.titleChartCardMonths),
      selected = chartMode == ChartViewMode.Months,
      onClick = { onChartModeSelected.invoke(ChartViewMode.Months) }
    )
    ChartButton(
      text = stringResource(R.string.titleChartCardHalfYear),
      selected = chartMode == ChartViewMode.SixMonths,
      onClick = { onChartModeSelected.invoke(ChartViewMode.SixMonths) }
    )
    ChartButton(
      text = stringResource(R.string.titleChartCardYear),
      selected = chartMode == ChartViewMode.Year,
      onClick = { onChartModeSelected.invoke(ChartViewMode.Year) }
    )
    ChartButton(
      text = stringResource(R.string.titleChartCardAll),
      selected = chartMode == ChartViewMode.All,
      onClick = { onChartModeSelected.invoke(ChartViewMode.All) }
    )
  }
}