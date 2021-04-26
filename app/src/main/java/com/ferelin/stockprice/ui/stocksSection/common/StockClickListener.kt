package com.ferelin.stockprice.ui.stocksSection.common

import com.ferelin.repository.adaptiveModels.AdaptiveCompany

interface StockClickListener {

    fun onFavouriteIconClicked(company: AdaptiveCompany)

    fun onHolderRebound(stockViewHolder: StockViewHolder)

    fun onHolderUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean)

    fun onStockClicked(stockViewHolder: StockViewHolder, company: AdaptiveCompany)
}