package com.ferelin.stockprice.utils

import com.ferelin.repository.adaptiveModels.AdaptiveCompany

interface StockClickListener {
    fun onFavouriteIconClicked(company: AdaptiveCompany)
    fun onStockClicked(company: AdaptiveCompany)
}