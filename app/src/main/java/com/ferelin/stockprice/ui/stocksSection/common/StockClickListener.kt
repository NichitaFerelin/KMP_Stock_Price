package com.ferelin.stockprice.ui.stocksSection.common

import com.ferelin.repository.adaptiveModels.AdaptiveCompany

interface StockClickListener {
    fun onFavouriteIconClicked(company: AdaptiveCompany)
    fun onStockClicked(company: AdaptiveCompany)
}