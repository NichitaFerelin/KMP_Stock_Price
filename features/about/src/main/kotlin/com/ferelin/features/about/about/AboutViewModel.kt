package com.ferelin.features.about.about

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.compare
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class AboutUiState(
    val companyProfile: CompanyProfileViewData = CompanyProfileViewData(),
    val companyProfileLce: LceState = LceState.None,
    val stockPrice: StockPriceViewData = StockPriceViewData(),
    val stockPriceLce: LceState = LceState.None,
    val stockPriceFetchLce: LceState = LceState.None,
)

internal class AboutViewModel(
    private val companyId: CompanyId,
    private val companyUseCase: CompanyUseCase,
    private val stockPriceUseCase: StockPriceUseCase,
    private val dispatchersProvider: DispatchersProvider,
    networkListener: NetworkListener
) : ViewModel() {
    private val viewModelState = MutableStateFlow(AboutUiState())
    val uiState = viewModelState.asStateFlow()

    init {
        companyUseCase.getBy(companyId)
            .combine(
                flow = companyUseCase.favoriteCompanies,
                transform = { company, favoriteCompanies ->
                    company.toProfile(
                        isFavorite = favoriteCompanies.find { it.company.id == companyId } != null
                    )
                }
            )
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onCompany)
            .launchIn(viewModelScope)

        companyUseCase.companiesLce
            .combine(
                flow = companyUseCase.favoriteCompaniesLce,
                transform = { lce1, lce2 -> lce1.compare(lce2) }
            )
            .onEach(this::onCompaniesLce)
            .launchIn(viewModelScope)

        stockPriceUseCase.getBy(companyId)
            .filterNotNull()
            .map { it.toStockPriceViewData() }
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onStockPrice)
            .launchIn(viewModelScope)

        stockPriceUseCase.stockPriceLce
            .onEach(this::onStockPriceLce)
            .launchIn(viewModelScope)

        stockPriceUseCase.stockPriceFetchLce
            .onEach(this::onStockPriceFetchLce)
            .launchIn(viewModelScope)

        networkListener.networkState
            .filter { available -> available }
            .onEach { onNetworkAvailable() }
            .launchIn(viewModelScope)
    }

    fun switchFavorite() {
        viewModelScope.launch(dispatchersProvider.IO) {
            val favorites = companyUseCase.favoriteCompanies.firstOrNull() ?: emptyList()
            val isFavorite = favorites.find { it.company.id == companyId } != null

            if (isFavorite) {
                companyUseCase.eraseFromFavorites(companyId)
            } else {
                companyUseCase.addToFavorites(companyId)
            }
        }
    }

    private fun fetchStockPrice() {
        viewModelScope.launch(dispatchersProvider.IO) {
            val profile = companyUseCase.getBy(companyId).first()
            stockPriceUseCase.fetchPrice(
                companyId = companyId,
                companyTicker = profile.ticker
            )
        }
    }

    private fun onNetworkAvailable() {
        fetchStockPrice()
    }

    private fun onCompany(companyProfile: CompanyProfileViewData) {
        viewModelState.update { it.copy(companyProfile = companyProfile) }
    }

    private fun onCompaniesLce(lceState: LceState) {
        viewModelState.update { it.copy(companyProfileLce = lceState) }
    }

    private fun onStockPrice(stockPriceViewData: StockPriceViewData) {
        viewModelState.update { it.copy(stockPrice = stockPriceViewData) }
    }

    private fun onStockPriceLce(lceState: LceState) {
        viewModelState.update { it.copy(stockPriceLce = lceState) }
    }

    private fun onStockPriceFetchLce(lceState: LceState) {
        viewModelState.update { it.copy(stockPriceFetchLce = lceState) }
    }
}