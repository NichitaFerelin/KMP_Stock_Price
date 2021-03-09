package com.ferelin.stockprice.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyQuote
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.common.StocksRecyclerAdapter
import com.ferelin.stockprice.utils.CoroutineContextProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseStocksViewModel(
    protected val mCoroutineContext: CoroutineContextProvider,
    protected val mDataInteractor: DataInteractor
) : ViewModel() {

    protected val mRecyclerAdapter = StocksRecyclerAdapter().apply {
        setOnBindCallback { _, company, position ->
            onBindCallback(company, position)
        }
    }
    val recyclerAdapter: StocksRecyclerAdapter
        get() = mRecyclerAdapter

    open fun initObservers() {}


    fun onFavouriteIconClicked(company: AdaptiveCompany) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (company.isFavourite) {
                mDataInteractor.removeCompanyFromFavourite(company)
            } else mDataInteractor.addCompanyToFavourite(company)
        }
    }

    fun onStockClicked(company: AdaptiveCompany) {

    }

    private fun onBindCallback(company: AdaptiveCompany, position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadCompanyQuote(company.symbol, position).collect { response ->
                onCompanyQuoteResponse(response)
            }
        }
    }

    private fun onCompanyQuoteResponse(response: AdaptiveCompanyQuote) {
        viewModelScope.launch(mCoroutineContext.IO) {
            response.company?.let { company ->
                val companyIndex = mRecyclerAdapter.companies.indexOf(company)
                if (companyIndex != -1) {
                    withContext(mCoroutineContext.Main) {
                        mRecyclerAdapter.updateCompany(company, companyIndex)
                    }
                }
            }
        }
    }
}