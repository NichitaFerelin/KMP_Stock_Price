package com.ferelin.stockprice.ui.common

import androidx.lifecycle.ViewModel
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.DataInteractor

class StocksViewModel(private val mDataInteractor: DataInteractor) : ViewModel() {

    private val mDefaultStockAdapter = StocksRecyclerAdapter()
    private val mFavouriteStocksAdapter = StocksRecyclerAdapter()

    fun getRecyclerAdapter(type: StocksAdapterType): StocksRecyclerAdapter {
        return when (type) {
            StocksAdapterType.Default -> mDefaultStockAdapter
            StocksAdapterType.Favourite -> mFavouriteStocksAdapter
        }
    }

    suspend fun onFavouriteIconClicked(company: AdaptiveCompany) {
        if (company.isFavourite) {
            mDataInteractor.removeCompanyFromFavourite(company)
        } else mDataInteractor.addCompanyToFavourite(company)
    }

    suspend fun onStockClicked(company: AdaptiveCompany) {

    }
}