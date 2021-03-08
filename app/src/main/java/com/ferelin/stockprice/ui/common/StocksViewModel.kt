package com.ferelin.stockprice.ui.common

import androidx.lifecycle.ViewModel
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveStockCandle
import com.ferelin.stockprice.dataInteractor.DataInteractor

class StocksViewModel(private val mDataInteractor: DataInteractor) : ViewModel() {

    private val mDefaultStockAdapter = StocksRecyclerAdapter()
    private val mFavouriteStocksAdapter = StocksRecyclerAdapter()
    private val mSearchStocksAdapter = StocksRecyclerAdapter()

    fun getRecyclerAdapter(type: StocksAdapterType): StocksRecyclerAdapter {
        return when (type) {
            StocksAdapterType.Default -> mDefaultStockAdapter
            StocksAdapterType.Favourite -> mFavouriteStocksAdapter
            StocksAdapterType.Search -> mSearchStocksAdapter
        }
    }

    suspend fun onFavouriteIconClicked(company: AdaptiveCompany) {
        if (company.isFavourite) {
            mDataInteractor.removeCompanyFromFavourite(company)
        } else mDataInteractor.addCompanyToFavourite(company)
    }

    suspend fun onStockClicked(company: AdaptiveCompany) {

    }

    suspend fun onStocksCandlesResponse(response: AdaptiveStockCandle, adapter: StocksAdapterType) {
        response.company?.let {
            getRecyclerAdapter(adapter).updateCompany(it)
        }
    }
}