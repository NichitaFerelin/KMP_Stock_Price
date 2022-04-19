package com.ferelin.features.cryptos.cryptos

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoId
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.ui.viewData.utils.toStrPrice

@Immutable
internal data class CryptoViewData(
    val id: CryptoId,
    val name: String,
    val logoUrl: String,
    val price: String,
    val profit: String
)

internal fun Crypto.toCryptoViewData(cryptoPrice: CryptoPrice?): CryptoViewData {
    return CryptoViewData(
        id = this.id,
        name = this.name,
        logoUrl = this.logoUrl,
        price = cryptoPrice?.price?.toStrPrice() ?: "",
        profit = formatProfitString(
            priceProfit = cryptoPrice?.priceChange.toString(),
            priceProfitPercents = cryptoPrice?.priceChangePercents.toString()
        )
    )
}

/**
 * formatProfitString(
 *      priceProfit = "3738.94748833",
 *      priceProfitPercents = "0.0593"
 *  ) = "+$3 738.94 (0,05%)"
 * */
private fun formatProfitString(
    priceProfit: String,
    priceProfitPercents: String
): String {
    if (
        priceProfit.length < 2
        || priceProfitPercents.length < 2
    ) return ""

    // If the profit is negative, then at the beginning there should be '-'
    // otherwise there will be the beginning of the number
    val prefix = if (priceProfit[0].isDigit()) {
        "+"
    } else "-"

    val profitStartIndex = if (prefix == "+") 0 else 1
    val profitResult = prefix + priceProfit.substring(profitStartIndex).toDouble().toStrPrice()

    val percents = priceProfitPercents.substring(profitStartIndex)
    val mainPart = percents.substringBefore(".")

    val remainder = priceProfitPercents.substringAfter('.', "")
    val remainderResult = if (remainder.length > 2) {
        remainder.substring(0, 2)
    } else remainder

    val secondPart = if (remainderResult.isEmpty()) {
        ""
    } else ",$remainderResult"
    return "$profitResult ($mainPart$secondPart%)"
}