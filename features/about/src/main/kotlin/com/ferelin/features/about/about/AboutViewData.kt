package com.ferelin.features.about.about

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.Company
import com.ferelin.core.domain.entity.StockPrice

@Immutable
internal data class CompanyProfileViewData(
    val name: String = "",
    val ticker: String = "",
    val logoUrl: String = "",
    val country: String = "",
    val industry: String = "",
    val phone: String = "",
    val webUrl: String = "",
    val capitalization: String = "",
    val isFavorite: Boolean = false
)

@Immutable
internal data class StockPriceViewData(
    val price: String = ""
)

internal fun Company.toProfile(isFavorite: Boolean): CompanyProfileViewData {
    return CompanyProfileViewData(
        name = this.name,
        ticker = this.ticker,
        logoUrl = this.logoUrl,
        country = this.country,
        industry = this.industry,
        phone = this.phone,
        webUrl = this.webUrl,
        capitalization = this.capitalization,
        isFavorite = isFavorite
    )
}

internal fun StockPrice.toStockPriceViewData(): StockPriceViewData {
    return StockPriceViewData(
        price = buildProfitString(
            currentPrice = this.currentPrice,
            previousPrice = this.previousClosePrice
        )
    )
}

/*
* buildProfitString (100.0, 50.0) = "+$50.0 (50,0%)"
* */
private fun buildProfitString(currentPrice: Double, previousPrice: Double): String {
    val numberProfit = currentPrice - previousPrice
    val numberProfitStr = numberProfit.toString()

    val digitNumberProfit = numberProfitStr.substringBefore('.').filter { it.isDigit() }
    val remainderNumberProfit = with(numberProfitStr.substringAfter('.')) {
        if (length >= 2) substring(0, 2) else this
    }

    val percentProfit = (100 * (currentPrice - previousPrice) / currentPrice).toString()
    val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
    val remainderPercentProfit = with(percentProfit.substringAfter('.')) {
        if (length >= 2) substring(0, 2) else this
    }

    val prefix = if (currentPrice > previousPrice) "+" else "-"
    return "$prefix$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
}