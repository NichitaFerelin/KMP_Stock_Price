package com.ferelin.core.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.entities.CompanyUseCase
import com.ferelin.core.domain.entities.FavouriteCompanyUseCase
import com.ferelin.core.domain.entities.StockPriceUseCase
import com.ferelin.core.ui.mapper.CompanyMapper
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.stocks.adapter.StockViewHolder
import com.ferelin.core.ui.view.stocks.adapter.createStocksAdapter
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE

open class StocksViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val stockPriceUseCase: StockPriceUseCase,
  private val stockStyleProvider: StockStyleProvider,
  protected val router: Router,
  companyUseCase: CompanyUseCase,
) : ViewModel() {
  val companies = companyUseCase.companies
    .combine(
      flow = favouriteCompanyUseCase.favouriteCompanies,
      transform = { companies, favouriteCompaniesIds ->
        CompanyMapper.map(companies, favouriteCompaniesIds)
      }
    )
    .combine(
      flow = stockPriceUseCase.stockPrice,
      transform = { viewData, stockPrices ->
        val pricesContainer = stockPrices.associateBy { it.id }
        viewData.map { stockViewData ->
          stockViewData.copy(
            stockPriceViewData = pricesContainer[stockViewData.id]?.let { StockPriceMapper.map(it) },
            style = stockStyleProvider.createStyle(stockViewData, pricesContainer[stockViewData.id])
          )
        }
      }
    )
  val companiesLce = companyUseCase.companiesLce

  val stocksAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createStocksAdapter(
        onStockClick = this::onStockClick,
        onFavouriteIconClick = this::onFavouriteIconClick,
        onBindCallback = this::onBind
      )
    ).apply { setHasStableIds(true) }
  }

  fun onHolderUntouched(stockViewHolder: StockViewHolder) {
    viewModelScope.launch {
      val viewData = stocksAdapter.getByPosition(stockViewHolder.layoutPosition)
      if (viewData is StockViewData) {
        onFavouriteIconClick(viewData)
      }
    }
  }

  private fun onStockClick(stockViewData: StockViewData) {
    // navigate
  }

  private fun onFavouriteIconClick(stockViewData: StockViewData) {
    viewModelScope.launch {
      if (stockViewData.isFavourite) {
        favouriteCompanyUseCase.addToFavourite(stockViewData.id)
      } else {
        favouriteCompanyUseCase.removeFromFavourite(stockViewData.id)
      }
    }
  }

  private fun onBind(stockViewData: StockViewData, position: Int) {
    viewModelScope.launch {
      stockPriceUseCase.fetchPrice(stockViewData.id, stockViewData.ticker)
    }
  }
}