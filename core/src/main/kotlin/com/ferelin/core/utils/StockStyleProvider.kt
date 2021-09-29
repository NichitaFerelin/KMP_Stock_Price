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

import com.ferelin.core.viewData.StockStyle
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.StockPrice
import javax.inject.Inject
import javax.inject.Named

class StockStyleProvider @Inject constructor(
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

    private fun getBackgroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) {
            mDrawableFavouriteBackgroundIconActive
        } else {
            mDrawableFavouriteBackgroundIcon
        }
    }

    private fun getForegroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) {
            mDrawableFavouriteForegroundIconActive
        } else {
            mDrawableFavouriteForegroundIcon
        }
    }

    private fun getProfitBackground(profit: String): Int {
        val prefix = profit.getOrNull(0)
        return if (prefix == '+') {
            mColorProfitPlus
        } else mColorProfitMinus
    }

    /**
     * Holder background depends of index at UI
     * */
    private fun getHolderBackground(index: Int): Int {
        return if (index % 2 == 0) {
            mColorHolderFirst
        } else mColorHolderSecond
    }

    private fun getRippleForeground(index: Int): Int {
        return if (index % 2 == 0) mDrawableRippleDark else mDrawableRippleLight
    }
}