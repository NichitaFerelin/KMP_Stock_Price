package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.FavoriteCompany
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
    companyUseCase: CompanyUseCase,
    dispatchersProvider: DispatchersProvider
) : ViewModel() {
    private val viewModelState = MutableStateFlow(HomeUiState())
    val uiState = viewModelState.asStateFlow()

    init {
        companyUseCase.companies
            .combine(
                flow = companyUseCase.favoriteCompanies,
                transform = { defaults, favorites ->
                    withFavoritesPriority(
                        defaults = filterNotFavorite(defaults, favorites),
                        favorites = favorites.reversed()
                    )
                }
            )
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onStocks)
            .launchIn(viewModelScope)

        companyUseCase.companiesLce
            .combine(
                flow = companyUseCase.favoriteCompaniesLce,
                transform = { companiesLce, favoritesLce ->
                    companiesLce.compare(favoritesLce)
                }
            )
            .flowOn(dispatchersProvider.IO)
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

private fun filterNotFavorite(
    default: List<Company>,
    favorites: List<FavoriteCompany>
): List<Company> {
    val favoritesIds = favorites.associateBy { it.company.id }
    return default.filter { favoritesIds[it.id] == null }
}

private fun withFavoritesPriority(
    defaults: List<Company>,
    favorites: List<FavoriteCompany>
): List<HomeStockViewData> {
    val favoritesResult = favorites.take(COMPANIES_FOR_PREVIEW).map(HomeViewDataMapper::map)
    val remainder = COMPANIES_FOR_PREVIEW - favoritesResult.size
    val defaultsResult = defaults.take(remainder).map(HomeViewDataMapper::map)
    return favoritesResult + defaultsResult
}