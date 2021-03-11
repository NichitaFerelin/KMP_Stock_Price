package com.ferelin.stockprice.dataInteractor.dataManager

import android.content.Context
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R

class DataStylesManager(private val mContext: Context) {

    private val mDrawableFavouriteIcon = R.drawable.ic_favourite
    private val mDrawableFavouriteIconActive = R.drawable.ic_favourite_active
    private val mColorProfitPlus = R.color.green
    private val mColorProfitMinus = R.color.red
    private val mColorHolderFirst = R.color.white
    private val mColorHolderSecond = R.color.whiteDark


    fun applyStyles(adaptiveCompany: AdaptiveCompany, index: Int) {
        adaptiveCompany.companyStyle.apply {
            holderBackground = getHolderBackground(index)
            favouriteIconResource = getIconDrawable(adaptiveCompany.isFavourite)
            dayProfitBackground = getProfitBackground(adaptiveCompany.companyDayData.profit)
        }
    }

    fun getIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) mDrawableFavouriteIconActive else mDrawableFavouriteIcon
    }

    fun getHolderBackground(index: Int): Int {
        return if (index % 2 == 0) {
            getColor(mColorHolderFirst)
        } else getColor(mColorHolderSecond)
    }

    fun getProfitBackground(profit: String): Int {
        val prefix = profit.getOrNull(0)
        return if (prefix == '+') {
            getColor(mColorProfitPlus)
        } else getColor(mColorProfitMinus)
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(mContext, color)
    }
}