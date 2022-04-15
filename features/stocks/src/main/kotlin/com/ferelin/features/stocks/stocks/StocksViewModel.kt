package com.ferelin.features.stocks.stocks

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.FavoriteCompany
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class StocksUiState(
    val stocks: List<StockViewData> = emptyList(),
    val stocksLce: LceState = LceState.None,
    val favoriteStocks: List<StockViewData> = emptyList(),
    val favoriteStocksLce: LceState = LceState.None
)

internal class StocksViewModel(
    private val companyUseCase: CompanyUseCase,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {
    private val viewModelState = MutableStateFlow(StocksUiState())
    val uiState = viewModelState.asStateFlow()

    init {
        companyUseCase.companies
            .combine(
                flow = companyUseCase.favoriteCompanies,
                transform = ::filterNotFavorite
            )
            .toStocksViewData()
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onCompanies)
            .launchIn(viewModelScope)

        companyUseCase.companiesLce
            .onEach(this::onCompaniesLce)
            .launchIn(viewModelScope)

        companyUseCase.favoriteCompanies
            .toFavStocksViewData()
            .map { it.reversed() }
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onFavoriteCompanies)
            .launchIn(viewModelScope)

        companyUseCase.favoriteCompaniesLce
            .onEach(this::onFavoriteCompaniesLce)
            .launchIn(viewModelScope)
    }

    fun switchFavorite(stockViewData: StockViewData) {
        viewModelScope.launch(dispatchersProvider.IO) {
            val companyId = stockViewData.id
            val favorites = companyUseCase.favoriteCompanies.firstOrNull() ?: emptyList()
            val isFavorite = favorites.find { it.company.id == companyId } != null

            if (isFavorite) {
                companyUseCase.eraseFromFavorites(companyId)
            } else {
                companyUseCase.addToFavorites(companyId)
            }
        }
    }

    private fun onCompanies(companies: List<StockViewData>) {
        viewModelState.update { it.copy(stocks = companies) }
    }

    private fun onCompaniesLce(lceState: LceState) {
        viewModelState.update { it.copy(stocksLce = lceState) }
    }

    private fun onFavoriteCompanies(companies: List<StockViewData>) {
        viewModelState.update { it.copy(favoriteStocks = companies) }
    }

    private fun onFavoriteCompaniesLce(lceState: LceState) {
        viewModelState.update { it.copy(favoriteStocksLce = lceState) }
    }
}

private fun filterNotFavorite(
    default: List<Company>,
    favorites: List<FavoriteCompany>
): List<Company> {
    val favoritesIds = favorites.associateBy { it.company.id }
    return default.filter { favoritesIds[it.id] == null }
}

private fun Flow<List<Company>>.toStocksViewData(): Flow<List<StockViewData>> {
    return this.map { companies ->
        companies.map { it.toStockViewData() }
    }
}

private fun Flow<List<FavoriteCompany>>.toFavStocksViewData(): Flow<List<StockViewData>> {
    return this.map { companies ->
        companies.map { it.toStockViewData() }
    }
}