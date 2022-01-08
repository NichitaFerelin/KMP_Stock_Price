package com.ferelin.core.ui.view.stocks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.ui.R
import com.ferelin.core.ui.databinding.ItemStockBinding
import com.ferelin.core.ui.view.adapter.BaseRecyclerViewHolder
import com.ferelin.core.ui.view.adapter.RecyclerAdapterDelegate
import com.ferelin.core.ui.viewData.StockViewData

fun createStocksAdapter(
  onStockClick: (StockViewData) -> Unit,
  onFavouriteIconClick: (StockViewData) -> Unit,
  onBindCallback: (StockViewData, Int) -> Unit
) = object : RecyclerAdapterDelegate(STOCK_VIEW_TYPE) {
  override fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder {
    return StockViewHolder(
      viewBinding = ItemStockBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
      onBind = { viewBinding, item, position, payloads ->
        item as StockViewData

        onBindCallback.invoke(item, position)

        with(viewBinding) {
          if (payloads.isEmpty()) {
            setCompanyInfo(item)
            setCompanyPrice(item)
            setFavourite(item)
            setBackground(item)
          } else {
            when (payloads[0]) {
              PAYLOAD_FAVOURITE_UPDATED -> setFavourite(item)
              PAYLOAD_PRICE_UPDATED -> setCompanyPrice(item)
            }
          }
        }
      }
    )
  }

  private fun ItemStockBinding.setBackground(item: StockViewData) {
    root.setCardBackgroundColor(
      ContextCompat.getColor(root.context, item.style.holderBackground)
    )
    root.foreground = ContextCompat.getDrawable(root.context, item.style.rippleForeground)
    root.setOnClickListener { onStockClick.invoke(item) }
    imageViewFavourite.setOnClickListener { onFavouriteIconClick.invoke(item) }

    Glide
      .with(root)
      .load(item.logoUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .error(
        AppCompatResources.getDrawable(
          rootLayout.context,
          R.drawable.ic_load_error_20
        )
      )
      .into(imageViewIcon)
  }
}

internal const val STOCK_VIEW_TYPE = 1
internal const val PAYLOAD_FAVOURITE_UPDATED = 1
internal const val PAYLOAD_PRICE_UPDATED = 2

internal fun ItemStockBinding.setCompanyInfo(item: StockViewData) {
  textViewCompanyName.text = item.name
  textViewCompanyTicker.text = item.ticker
}

internal fun ItemStockBinding.setFavourite(item: StockViewData) {
  imageViewFavourite.contentDescription = item.style.iconContentDescription
  imageViewFavourite.setImageResource(item.style.favouriteBackgroundIcon)
  imageViewBoundedIcon.setImageResource(item.style.favouriteForegroundIcon)
}

internal fun ItemStockBinding.setCompanyPrice(item: StockViewData) {
  textViewCurrentPrice.text = item.stockPriceViewData?.price ?: ""
  textViewDayProfit.text = item.stockPriceViewData?.profit ?: ""
  textViewDayProfit.setTextColor(
    ContextCompat.getColor(root.context, item.style.dayProfitBackground)
  )
}