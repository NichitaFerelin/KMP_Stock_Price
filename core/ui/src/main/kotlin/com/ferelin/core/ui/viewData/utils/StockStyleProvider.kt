package com.ferelin.core.ui.viewData.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.ui.R
import com.ferelin.core.ui.viewData.StockStyle
import com.ferelin.core.ui.viewData.StockViewData
import javax.inject.Inject

class StockStyleProvider @Inject constructor(
  private val context: Context
) {
  fun createStyle(
    stockViewData: StockViewData,
    stockPrice: StockPrice?
  ): StockStyle {
    val currentPrice = stockPrice?.currentPrice ?: 0.0
    val previousClosePrice = stockPrice?.previousClosePrice ?: 0.0
    return StockStyle(
      holderBackground = getHolderBackground(stockViewData.id.value),
      rippleForeground = getRippleForeground(stockViewData.id.value),
      favouriteBackgroundIcon = getBackgroundIconDrawable(stockViewData.isFavourite),
      favouriteForegroundIcon = getForegroundIconDrawable(stockViewData.isFavourite),
      dayProfitBackground = getProfitBackground(currentPrice - previousClosePrice),
      iconContentDescription = getContentDescription(stockViewData.isFavourite)
    )
  }

  private fun getForegroundIconDrawable(isFavourite: Boolean): Int {
    return if (isFavourite) {
      drawableFavouriteForegroundIconActive
    } else drawableFavouriteForegroundIcon
  }

  private fun getContentDescription(isFavourite: Boolean): String {
    return context.getString(
      if (isFavourite) {
        R.string.descriptionRemoveFromFavourites
      } else {
        R.string.descriptionAddToFavourites
      }
    )
  }

  private fun getProfitBackground(profit: Double): Int {
    return if (profit > 0) {
      getColor(colorProfitPlus)
    } else getColor(colorProfitMinus)
  }

  private fun getBackgroundIconDrawable(isFavourite: Boolean): Int {
    return if (isFavourite) {
      drawableFavouriteBackgroundIconActive
    } else drawableFavouriteBackgroundIcon
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

internal val drawableFavouriteBackgroundIcon: Int = R.drawable.ic_favourite_16
internal val drawableFavouriteBackgroundIconActive: Int = R.drawable.ic_favourite_active_16
internal val drawableFavouriteForegroundIcon: Int = R.drawable.ic_star_20x19
internal val drawableFavouriteForegroundIconActive: Int = R.drawable.ic_star_active_20x19
internal val drawableRippleLight: Int = R.drawable.ripple_light
internal val drawableRippleDark: Int = R.drawable.ripple_dark
internal val colorProfitPlus: Int = R.color.profitPlus
internal val colorProfitMinus: Int = R.color.profitMinus
internal val colorHolderFirst: Int = R.color.white
internal val colorHolderSecond: Int = R.color.whiteDark