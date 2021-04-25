package com.ferelin.stockprice.ui.stocksSection.common

import android.animation.Animator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.utils.anim.AnimatorManager

class StockItemAnimator : DefaultItemAnimator() {

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        if (newHolder !is StockViewHolder
            || oldHolder !is StockViewHolder
            || preInfo !is StockHolderInfo
        ) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }

        when {
            isFirstTimePriceLoad(preInfo, newHolder) -> animatePriceFade(newHolder)
            isPriceChanged(preInfo, newHolder) -> animatePriceChanges(newHolder)
            else -> animateStar(newHolder)
        }

        return true
    }

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            val priceStr =
                (viewHolder as StockViewHolder).binding.textViewCurrentPrice.text.toString()
            return StockHolderInfo(priceStr)
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ): Boolean = true

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean = true

    private fun isFirstTimePriceLoad(
        preInfo: StockHolderInfo,
        newHolder:StockViewHolder
    ): Boolean {
        return preInfo.price.isEmpty() && newHolder.binding.textViewCurrentPrice.text.isNotEmpty()
    }

    private fun isPriceChanged(
        preInfo: StockHolderInfo,
        newHolder:StockViewHolder
    ): Boolean {
        return preInfo.price != newHolder.binding.textViewCurrentPrice.text
    }

    private fun animateStar(holder: StockViewHolder) {
        holder.binding.imageViewFavourite
            .animate()
            .scaleY(1.2F)
            .scaleX(1.2F)
            .setDuration(175L)
            .setListener(object : AnimatorManager() {
                override fun onAnimationEnd(animation: Animator?) {
                    dispatchAnimationFinished(holder)
                    holder.binding.imageViewFavourite
                        .animate()
                        .scaleY(1F)
                        .scaleX(1F)
                        .setDuration(175L)
                        .setListener(object : AnimatorManager() {
                            override fun onAnimationEnd(animation: Animator?) {
                                dispatchAnimationFinished(holder)
                            }
                        })
                        .start()
                }
            })
            .start()
    }

    private fun animatePriceFade(holder: StockViewHolder) {
        holder.binding.textViewCurrentPrice.alpha = 0F
        holder.binding.textViewDayProfit.alpha = 0F

        holder.binding.textViewCurrentPrice
            .animate()
            .alpha(1F)
            .setDuration(300L)
            .start()
        holder.binding.textViewDayProfit
            .animate()
            .alpha(1F)
            .setDuration(300L)
            .setListener(object : AnimatorManager() {
                override fun onAnimationEnd(animation: Animator?) {
                    dispatchAnimationFinished(holder)
                }
            })
            .start()
    }

    private fun animatePriceChanges(holder: StockViewHolder) {
        holder.binding.textViewCurrentPrice
            .animate()
            .scaleY(1.2F)
            .scaleX(1.2F)
            .setDuration(175L)
            .setListener(object : AnimatorManager() {
                override fun onAnimationEnd(animation: Animator?) {
                    dispatchAnimationFinished(holder)
                    holder.binding.textViewCurrentPrice
                        .animate()
                        .scaleY(1F)
                        .scaleX(1F)
                        .setDuration(175L)
                        .start()
                }
            })
            .start()
        holder.binding.textViewDayProfit
            .animate()
            .scaleY(1.2F)
            .scaleX(1.2F)
            .setDuration(175L)
            .setListener(object : AnimatorManager() {
                override fun onAnimationEnd(animation: Animator?) {
                    dispatchAnimationFinished(holder)
                    holder.binding.textViewDayProfit
                        .animate()
                        .scaleY(1F)
                        .scaleX(1F)
                        .setDuration(175L)
                        .setListener(object : AnimatorManager() {
                            override fun onAnimationEnd(animation: Animator?) {
                                dispatchAnimationFinished(holder)
                            }
                        })
                        .start()
                }
            })
            .start()
    }
}