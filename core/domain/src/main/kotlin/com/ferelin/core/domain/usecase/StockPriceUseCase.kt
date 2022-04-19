package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.*

interface StockPriceUseCase {
    val stockPriceLce: Flow<LceState>
    val stockPriceFetchLce: Flow<LceState>
    fun getBy(companyId: CompanyId): Flow<StockPrice?>
    suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
}

internal class StockPriceUseCaseImpl(
    private val stockPriceRepository: StockPriceRepository,
    private val dispatchersProvider: DispatchersProvider
) : StockPriceUseCase {

    override fun getBy(companyId: CompanyId): Flow<StockPrice?> {
        return stockPriceRepository.getBy(companyId)
            .onStart { stockPriceLceState.value = LceState.Loading }
            .onEach { stockPriceLceState.value = LceState.Content }
            .catch { e -> stockPriceLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)
    }

    private val stockPriceLceState = MutableStateFlow<LceState>(LceState.None)
    override val stockPriceLce: Flow<LceState> = stockPriceLceState.asStateFlow()

    override suspend fun fetchPrice(companyId: CompanyId, companyTicker: String) {
        stockPriceFetchLceState.value = LceState.Loading
        stockPriceRepository.fetchPrice(companyId, companyTicker)
            .onSuccess { stockPriceFetchLceState.value = LceState.Content }
            .onFailure { stockPriceFetchLceState.value = LceState.Error(it.message) }
    }

    private val stockPriceFetchLceState = MutableStateFlow<LceState>(LceState.None)
    override val stockPriceFetchLce: Flow<LceState> = stockPriceFetchLceState.asStateFlow()
}