package com.ferelin.core.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.mapper.CompanyMapper
import com.ferelin.core.ui.viewData.StockViewData
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch

open class BaseStocksViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  protected val dispatchersProvider: DispatchersProvider,
  companyUseCase: CompanyUseCase
) : ViewModel() {

  protected val companies: Observable<List<StockViewData>> = Observable.combineLatest(
    companyUseCase.companies,
    favouriteCompanyUseCase.favouriteCompanies
  ) { companies, favouriteCompaniesIds ->
    CompanyMapper.map(companies, favouriteCompaniesIds)
  }

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