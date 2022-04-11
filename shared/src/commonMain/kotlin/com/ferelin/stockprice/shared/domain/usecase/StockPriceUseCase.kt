package com.ferelin.stockprice.shared.domain.usecase

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.entity.StockPrice
import com.ferelin.stockprice.shared.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.*

interface StockPriceUseCase {
    val stockPrice: Flow<List<StockPrice>>
    suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
    val stockPriceLce: Flow<LceState>
}

internal class StockPriceUseCaseImpl(
    private val stockPriceRepository: StockPriceRepository
) : StockPriceUseCase {
    override val stockPrice: Flow<List<StockPrice>> = stockPriceRepository.stockPrice
        .zip(
            other = stockPriceRepository.fetchError,
            transform = { stockPrices, exception ->
                if (exception != null) {
                    stockPriceLceState.value = LceState.Error(exception.message)
                }
                stockPrices
            }
        )
        .onStart { stockPriceLceState.value = LceState.Loading }
        .onEach { stockPriceLceState.value = LceState.Content }
        .catch { e -> stockPriceLceState.value = LceState.Error(e.message) }

    override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
        stockPriceRepository.fetchPrice(companyId, companyTicker)
    }

    private val stockPriceLceState = MutableStateFlow<LceState>(LceState.None)
    override val stockPriceLce: Flow<LceState> = stockPriceLceState.asStateFlow()
}