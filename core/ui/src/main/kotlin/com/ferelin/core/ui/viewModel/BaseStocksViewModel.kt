package com.ferelin.core.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.mapper.CompanyMapper
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.stocks.adapter.createStocksAdapter
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE

abstract class BaseStocksViewModel(
  private val companyUseCase: CompanyUseCase,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val stockStyleProvider: StockStyleProvider,
  protected val dispatchersProvider: DispatchersProvider
) : ViewModel() {
  abstract fun onStockClick(stockViewData: StockViewData)

  val companies = companyUseCase.companies
    .combine(
      flow = favouriteCompanyUseCase.favouriteCompanies,
      transform = { companies, favouriteCompaniesIds ->
        val companiesViewData = CompanyMapper.map(companies, favouriteCompaniesIds)
        companiesViewData.mapIndexed { index, stockViewData ->
          stockViewData.copy(
            style = stockStyleProvider.createStyle(stockViewData, null, index)
          )
        }
      }
    )
  val companiesLce = companyUseCase.companiesLce

  val stocksAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createStocksAdapter(this::onStockClick, this::onFavouriteIconClick)
    )
  }


  private fun onFavouriteIconClick(stockViewData: StockViewData) {
    viewModelScope.launch(dispatchersProvider.IO) {
      if (stockViewData.isFavourite) {
        favouriteCompanyUseCase.removeFromFavourite(stockViewData.id)
      } else {
        favouriteCompanyUseCase.addToFavourite(stockViewData.id)
      }
    }
  }
}