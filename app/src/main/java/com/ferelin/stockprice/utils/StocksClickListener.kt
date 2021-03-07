package com.ferelin.stockprice.utils

import com.ferelin.repository.adaptiveModels.AdaptiveCompany

interface StocksClickListener {
    fun onFavouriteIconClicked(company: AdaptiveCompany)
    fun onStockClicked(company: AdaptiveCompany)
}