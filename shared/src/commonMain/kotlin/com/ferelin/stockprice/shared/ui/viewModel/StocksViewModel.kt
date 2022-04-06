package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.androidApp.domain.usecase.CompanyUseCase
import com.ferelin.stockprice.androidApp.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.stockprice.androidApp.domain.entity.LceState
import com.ferelin.stockprice.androidApp.ui.DispatchersProvider
import com.ferelin.stockprice.androidApp.ui.viewData.StockViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

data class StocksStateUi(
  val companies: List<StockViewData> = emptyList(),
  val companiesLce: LceState = LceState.None
)

class StocksViewModel(
  companyUseCase: CompanyUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  viewModelScope: CoroutineScope,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  favouriteCompanyUseCase,
  viewModelScope,
  dispatchersProvider,
  companyUseCase
) {
  private val viewModelState = MutableStateFlow(StocksStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companies
      .onEach(this::onCompanies)
      .launchIn(viewModelScope)

    companyUseCase.companiesLce
      .onEach(this::onCompaniesLce)
      .launchIn(viewModelScope)
  }

  private fun onCompanies(companies: List<StockViewData>) {
    viewModelState.update { it.copy(companies = companies) }
  }

  private fun onCompaniesLce(lceState: LceState) {
    viewModelState.update { it.copy(companiesLce = lceState) }
  }
}