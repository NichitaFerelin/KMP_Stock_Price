package com.ferelin.features.stocks.defaults

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Immutable
internal data class DefaultStocksStateUi(
  val companies: List<StockViewData> = emptyList(),
  val companiesLce: LceState = LceState.None
)

internal class DefaultStocksViewModel(
  companyUseCase: CompanyUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  favouriteCompanyUseCase,
  dispatchersProvider,
  companyUseCase
) {
  private val viewModelState = MutableStateFlow(DefaultStocksStateUi())
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

internal class DefaultStocksViewModelFactory @Inject constructor(
  private val dispatchersProvider: DispatchersProvider,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val companyUseCase: CompanyUseCase
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == DefaultStocksViewModel::class.java)
    return DefaultStocksViewModel(companyUseCase, favouriteCompanyUseCase, dispatchersProvider) as T
  }
}