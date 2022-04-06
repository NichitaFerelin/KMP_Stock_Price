package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.shared.domain.usecase.CompanyUseCase
import com.ferelin.stockprice.shared.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.mapper.CompanyMapper
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

open class BaseStocksViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  protected val viewModelScope: CoroutineScope,
  protected val dispatchersProvider: DispatchersProvider,
  companyUseCase: CompanyUseCase
) {
  protected val companies = companyUseCase.companies
    .combine(
      flow = favouriteCompanyUseCase.favouriteCompanies,
      transform = { companies, favouriteCompaniesIds ->
        CompanyMapper.map(companies, favouriteCompaniesIds)
      }
    )
    .flowOn(dispatchersProvider.IO)

  fun onFavouriteIconClick(stockViewData: StockViewData) {
    viewModelScope.launch(dispatchersProvider.IO) {
      if (stockViewData.isFavourite) {
        favouriteCompanyUseCase.removeFromFavourite(stockViewData.id)
      } else {
        favouriteCompanyUseCase.addToFavourite(stockViewData.id)
      }
    }
  }
}