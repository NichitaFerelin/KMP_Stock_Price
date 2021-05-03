package com.ferelin.stockprice.ui.stocksSection.common

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.R
import com.ferelin.stockprice.utils.anim.AnimationManager
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
            isFirstTimePriceLoad(preInfo, newHolder) -> animatePriceFadeIn(newHolder)
            isPriceChanged(preInfo, newHolder) -> animatePriceChanges(newHolder)
            else -> animateStar(newHolder)
        }

        return true
    }

    override fun isRunning(): Boolean = false

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

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        if (item is StockViewHolder) {
            item.apply {
                attachedPriceAnimator?.cancel()
                attachedPriceAnimator = null

                attachedProfitAnimator?.cancel()
                attachedProfitAnimator = null

                attachedStartAnimator?.cancel()
                attachedProfitAnimator = null

                attachedPriceFadeAnimation?.cancel()
                attachedPriceFadeAnimation = null
            }
        }
    }

    private fun isFirstTimePriceLoad(
        preInfo: StockHolderInfo,
        newHolder: StockViewHolder
    ): Boolean {
        return preInfo.price.isEmpty() && newHolder.binding.textViewCurrentPrice.text.isNotEmpty()
    }

    private fun isPriceChanged(
        preInfo: StockHolderInfo,
        newHolder: StockViewHolder
    ): Boolean {
        return preInfo.price != newHolder.binding.textViewCurrentPrice.text
    }

    private fun animateStar(holder: StockViewHolder) {

        val scaleInOut =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)
        scaleInOut.setTarget(holder.binding.imageViewFavourite)
        scaleInOut.addListener(object : AnimatorManager() {
            override fun onAnimationEnd(animation: Animator?) {
                dispatchAnimationFinished(holder)
            }
        })

        holder.attachedStartAnimator = scaleInOut
        scaleInOut.start()
    }

    private fun animatePriceFadeIn(holder: StockViewHolder) {

        val fadeIn = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        fadeIn.setAnimationListener(object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                holder.binding.textViewCurrentPrice.alpha = 1F
                holder.binding.textViewDayProfit.alpha = 1F
                fadeIn.setAnimationListener(null)
                dispatchAnimationFinished(holder)
            }
        })
        holder.attachedPriceFadeAnimation = fadeIn
        holder.binding.textViewCurrentPrice.startAnimation(fadeIn)
        holder.binding.textViewDayProfit.startAnimation(fadeIn)
    }

    private fun animatePriceChanges(holder: StockViewHolder) {

        val scaleInOutPrice =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)
        val scaleInOutProfit =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)

        scaleInOutPrice.addListener(object : AnimatorManager() {
            override fun onAnimationEnd(animation: Animator?) {
                dispatchAnimationFinished(holder)
            }
        })

        holder.attachedPriceAnimator = scaleInOutPrice
        holder.attachedProfitAnimator = scaleInOutProfit

        scaleInOutPrice.setTarget(holder.binding.textViewCurrentPrice)
        scaleInOutProfit.setTarget(holder.binding.textViewDayProfit)

        scaleInOutPrice.start()
        scaleInOutProfit.start()
    }
}