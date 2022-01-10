package com.ferelin.core.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.ui.mapper.CompanyMapper
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.view.stocks.adapter.StockViewHolder
import com.ferelin.core.ui.view.stocks.adapter.createStocksAdapter
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

open class StocksViewModel @Inject constructor(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val stockPriceUseCase: StockPriceUseCase,
  private val stockStyleProvider: StockStyleProvider,
  protected val coordinator: Coordinator,
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

  open fun onStockClick(stockViewData: StockViewData) {

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
}