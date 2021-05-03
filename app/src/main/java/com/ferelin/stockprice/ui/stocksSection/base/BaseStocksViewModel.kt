package com.ferelin.stockprice.ui.stocksSection.base

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseStocksViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    protected val mStocksRecyclerAdapter = StocksRecyclerAdapter().apply {
        setHasStableIds(true)
        setOnBindCallback { _, company, position ->
            onItemBind(company, position)
        }
    }
    val stocksRecyclerAdapter: StocksRecyclerAdapter
        get() = mStocksRecyclerAdapter

    private val mEventCompanyChanged = MutableSharedFlow<AdaptiveCompany>()
    val eventCompanyChanged: SharedFlow<AdaptiveCompany>
        get() = mEventCompanyChanged

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            collectSharedCompaniesUpdates()
        }
    }

    fun onFavouriteIconClicked(company: AdaptiveCompany) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (company.isFavourite) {
                mDataInteractor.removeCompanyFromFavourite(company)
            } else mDataInteractor.addCompanyToFavourite(company)
        }
    }

    private fun onItemBind(company: AdaptiveCompany, position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadCompanyQuote(company.companyProfile.symbol, position).collect()
        }
    }

    private suspend fun collectSharedCompaniesUpdates() {
        mDataInteractor.sharedCompaniesUpdates.collect { mEventCompanyChanged.emit(it.data!!) }
    }
}