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
import com.ferelin.core.viewData.StockStyle
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.StockPrice
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class StockStyleProvider @Inject constructor(
    private val mContext: Context,
    @Named("BackIcon") private val mDrawableFavouriteBackgroundIcon: Int,
    @Named("BackIconActive") private val mDrawableFavouriteBackgroundIconActive: Int,
    @Named("ForeIcon") private val mDrawableFavouriteForegroundIcon: Int,
    @Named("ForeIconActive") private val mDrawableFavouriteForegroundIconActive: Int,
    @Named("RippleLight") private val mDrawableRippleLight: Int,
    @Named("RippleDark") private val mDrawableRippleDark: Int,
    @Named("ProfitPlus") private val mColorProfitPlus: Int,
    @Named("ProfitMinus") private val mColorProfitMinus: Int,
    @Named("HolderFirst") private val mColorHolderFirst: Int,
    @Named("HolderSecond") private val mColorHolderSecond: Int
) {
    fun createStyle(company: Company, stockPrice: StockPrice?): StockStyle {
        return StockStyle(
            holderBackground = getHolderBackground(company.id),
            rippleForeground = getRippleForeground(company.id),
            favouriteBackgroundIconResource = getBackgroundIconDrawable(company.isFavourite),
            favouriteForegroundIconResource = getForegroundIconDrawable(company.isFavourite),
            dayProfitBackground = stockPrice?.let { getProfitBackground(it.profit) } ?: 0
        )
    }

    fun updateProfit(stockViewData: StockViewData) {
        stockViewData.stockPrice?.let { stockPrice ->
            stockViewData.style.dayProfitBackground = getProfitBackground(stockPrice.profit)
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
            mDrawableFavouriteForegroundIconActive
        } else {
            mDrawableFavouriteForegroundIcon
        }
    }

    fun getProfitBackground(profit: String): Int {
        val prefix = profit.getOrNull(0)
        return if (prefix == '+') {
            getColor(mColorProfitPlus)
        } else getColor(mColorProfitMinus)
    }

    private fun getBackgroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) {
            mDrawableFavouriteBackgroundIconActive
        } else {
            mDrawableFavouriteBackgroundIcon
        }
    }

    private fun getHolderBackground(index: Int): Int {
        return if (index % 2 == 0) {
            getColor(mColorHolderFirst)
        } else getColor(mColorHolderSecond)
    }

    private fun getRippleForeground(index: Int): Int {
        return if (index % 2 == 0) mDrawableRippleDark else mDrawableRippleLight
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(mContext, color)
    }
}