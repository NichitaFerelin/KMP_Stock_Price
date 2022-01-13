package com.ferelin.features.stocks.defaults

import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StocksStateUi(
  val companies: List<StockViewData> = emptyList(),
  val companiesLce: LceState = LceState.None
)

class StocksViewModel @Inject constructor(
  companyUseCase: CompanyUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  companyUseCase,
  favouriteCompanyUseCase,
  dispatchersProvider
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