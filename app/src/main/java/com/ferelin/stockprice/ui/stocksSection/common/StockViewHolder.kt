package com.ferelin.stockprice.ui.stocksSection.common

import android.animation.Animator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.databinding.ItemStockBinding

class StockViewHolder private constructor(
    val binding: ItemStockBinding,
    var attachedPriceAnimator: Animator? = null,
    var attachedProfitAnimator: Animator? = null,
    var attachedPriceFadeAnimation: Animation? = null,
    var attachedStartAnimator: Animator? = null
) : RecyclerView.ViewHolder(binding.root) {

    var company: AdaptiveCompany? = null

    fun bind(item: AdaptiveCompany) {
        company = item
        bindData(item)

        Glide
            .with(binding.root)
            .load(item.companyProfile.logoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageViewIcon)
    }

    private fun bindData(item: AdaptiveCompany) {
        binding.apply {
            textViewCompanyName.text = item.companyProfile.name
            textViewCompanySymbol.text = item.companyProfile.symbol
            textViewCurrentPrice.text = item.companyDayData.currentPrice
            textViewDayProfit.text = item.companyDayData.profit
            textViewDayProfit.setTextColor(item.companyStyle.dayProfitBackground)
            imageViewFavourite.setImageResource(item.companyStyle.favouriteDefaultIconResource)
            imageViewBoundedIcon.setImageResource(item.companyStyle.favouriteSingleIconResource)

            // Tag to identify image for animation at BaseStocksFragment
            imageViewBoundedIcon.tag = item.companyStyle.favouriteSingleIconResource

            root.setCardBackgroundColor(item.companyStyle.holderBackground)
            root.foreground =
                ContextCompat.getDrawable(root.context, item.companyStyle.rippleForeground)
            root.transitionName = "root_${item.id}"
        }
    }

    companion object {
        fun from(parent: ViewGroup): StockViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemStockBinding.inflate(inflater, parent, false)
            return StockViewHolder(binding)
        }
    }
}