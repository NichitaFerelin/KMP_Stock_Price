package com.ferelin.features.stocks.favourites

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
internal data class FavouriteStocksStateUi(
  val companies: List<StockViewData> = emptyList(),
  val companiesLce: LceState = LceState.None
)

internal class FavouriteStocksViewModel(
  companyUseCase: CompanyUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  companyUseCase,
  favouriteCompanyUseCase,
  dispatchersProvider
) {
  private val viewModelState = MutableStateFlow(FavouriteStocksStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companies
      .filterFavouritesOnly()
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

internal fun Flow<List<StockViewData>>.filterFavouritesOnly(): Flow<List<StockViewData>> {
  return this.map { companies ->
    companies.filter { stock -> stock.isFavourite }
  }
}

internal class FavouriteStocksViewModelFactory @Inject constructor(
  private val dispatchersProvider: DispatchersProvider,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val companyUseCase: CompanyUseCase
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == FavouriteStocksViewModel::class.java)
    return FavouriteStocksViewModel(companyUseCase, favouriteCompanyUseCase, dispatchersProvider) as T
  }
}