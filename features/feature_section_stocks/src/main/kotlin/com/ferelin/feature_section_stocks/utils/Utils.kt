/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.feature_section_stocks.utils

import com.ferelin.core.utils.toStrPrice

/**
 * Formats two string into "profit" string
 *
 * formatProfitString(
 *      priceProfit = "3738.94748833",
 *      priceProfitPercents = "0.0593"
 *  )
 *  Return = "+$3 738.94 (0,05%)"
 * */
fun formatProfitString(priceProfit: String, priceProfitPercents: String): String {

    // Wrong input data
    if (
        priceProfit.length < 2
        || priceProfitPercents.length < 2
    ) {
        return ""
    }

    // If the profit is negative, then at the beginning there should be '-'
    // otherwise there will be the beginning of the number
    val prefix = if (priceProfit[0].isDigit()) {
        "+"
    } else {
        "-"
    }

    val profitStartIndex = if (prefix == "+") {
        0
    } else {
        1
    }

    val profitResult = prefix +
            priceProfit.substring(profitStartIndex).toDouble().toStrPrice()

    val percents = priceProfitPercents.substring(profitStartIndex)
    val mainPart = percents.substringBefore(".")

    val remainder = priceProfitPercents.substringAfter('.', "")
    val remainderResult = if (remainder.length > 2) {
        remainder.substring(0, 2)
    } else {
        remainder
    }

    val secondPart = if (remainderResult.isEmpty()) {
        ""
    } else {
        ",$remainderResult"
    }
    return "$profitResult ($mainPart$secondPart%)"
}