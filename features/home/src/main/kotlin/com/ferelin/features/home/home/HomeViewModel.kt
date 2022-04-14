package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.compare
import com.ferelin.core.domain.usecase.CompanyUseCase
import kotlinx.coroutines.flow.*

@Immutable
internal data class HomeUiState(
    val stocks: List<HomeStockViewData> = emptyList(),
    val stocksLce: LceState = LceState.None
)

internal class HomeViewModel(
    companyUseCase: CompanyUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(HomeUiState())
    val uiState = viewModelState.asStateFlow()

    init {
        companyUseCase.companies
            .combine(
                flow = companyUseCase.favouriteCompanies,
                transform = ::reduceWithFavouritesPriority
            )
            .onEach(this::onStocks)
            .launchIn(viewModelScope)

        companyUseCase.companiesLce
            .zip(
                other = companyUseCase.favouriteCompaniesLce,
                transform = { companiesLce, favouritesLce ->
                    companiesLce.compare(favouritesLce)
                }
            )
            .onEach(this::onStocksLce)
            .launchIn(viewModelScope)
    }

    private fun onStocks(companies: List<HomeStockViewData>) {
        viewModelState.update { it.copy(stocks = companies) }
    }

    private fun onStocksLce(lceState: LceState) {
        viewModelState.update { it.copy(stocksLce = lceState) }
    }
}

private fun reduceWithFavouritesPriority(
    companies: List<Company>,
    favourites: List<Company>
): List<HomeStockViewData> {
    val companiesForPreview = if (favourites.size >= COMPANIES_FOR_PREVIEW) {
        favourites
    } else {
        (favourites + companies).take(COMPANIES_FOR_PREVIEW)
    }
    return companiesForPreview.map(HomeViewDataMapper::map)
}