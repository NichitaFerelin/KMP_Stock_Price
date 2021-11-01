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

package com.ferelin.core.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.ferelin.core.R
import com.ferelin.core.viewData.StockStyle
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.StockPrice
import javax.inject.Inject

class StockStyleProvider @Inject constructor(
    private val context: Context
) {
    private val drawableFavouriteBackgroundIcon: Int = R.drawable.ic_favourite
    private val drawableFavouriteBackgroundIconActive: Int = R.drawable.ic_favourite_active
    private val drawableFavouriteForegroundIcon: Int = R.drawable.ic_star
    private val drawableFavouriteForegroundIconActive: Int = R.drawable.ic_star_active
    private val drawableRippleLight: Int = R.drawable.ripple_light
    private val drawableRippleDark: Int = R.drawable.ripple_dark
    private val colorProfitPlus: Int = R.color.profitPlus
    private val colorProfitMinus: Int = R.color.profitMinus
    private val colorHolderFirst: Int = R.color.white
    private val colorHolderSecond: Int = R.color.whiteDark

    fun createStyle(company: Company, stockPrice: StockPrice?): StockStyle {
        return StockStyle(
            holderBackground = getHolderBackground(company.id),
            rippleForeground = getRippleForeground(company.id),
            favouriteBackgroundIconResource = getBackgroundIconDrawable(company.isFavourite),
            favouriteForegroundIconResource = getForegroundIconDrawable(company.isFavourite),
            dayProfitBackground = stockPrice?.let {
                getProfitBackground(it.currentPrice - it.previousClosePrice)
            } ?: 0
        )
    }

    fun updateProfit(stockViewData: StockViewData) {
        stockViewData.stockPriceViewData?.let { stockPrice ->
            stockViewData.style.dayProfitBackground =
                getProfitBackground(stockPrice.currentPrice - stockPrice.previousClosePrice)
        }
    }

    fun updateFavourite(stockViewData: StockViewData) {
        stockViewData.style.apply {
            favouriteBackgroundIconResource = getBackgroundIconDrawable(stockViewData.isFavourite)
            favouriteForegroundIconResource = getForegroundIconDrawable(stockViewData.isFavourite)
        }
    }

    fun getForegroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) {
            drawableFavouriteForegroundIconActive
        } else {
            drawableFavouriteForegroundIcon
        }
    }

    fun getProfitBackground(profit: String): Int {
        val prefix = profit.getOrNull(0)
        return if (prefix == '+') {
            getColor(colorProfitPlus)
        } else getColor(colorProfitMinus)
    }

    private fun getProfitBackground(profit: Double): Int {
        return if (profit > 0) {
            getColor(colorProfitPlus)
        } else {
            getColor(colorProfitMinus)
        }
    }

    private fun getBackgroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) {
            drawableFavouriteBackgroundIconActive
        } else {
            drawableFavouriteBackgroundIcon
        }
    }

    private fun getHolderBackground(index: Int): Int {
        return if (index % 2 == 0) {
            getColor(colorHolderFirst)
        } else getColor(colorHolderSecond)
    }

    private fun getRippleForeground(index: Int): Int {
        return if (index % 2 == 0) drawableRippleDark else drawableRippleLight
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(context, color)
    }
}