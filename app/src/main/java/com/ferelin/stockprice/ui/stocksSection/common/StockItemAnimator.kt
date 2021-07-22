package com.ferelin.stockprice.ui.stocksSection.common

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

import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.R
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.invalidate

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

        dispatchAnimationFinished(newHolder)

        when {
            isFirstTimePriceLoad(preInfo, newHolder) -> animatePriceFadeIn(newHolder)
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
        if (changeFlags == FLAG_CHANGED && viewHolder is StockViewHolder) {
            val priceStr = viewHolder.binding.textViewCurrentPrice.text.toString()
            return StockHolderInfo(priceStr)
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean = true

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        if (item is StockViewHolder) {
            item.apply {
                attachedPriceAnimator?.invalidate()
                attachedProfitAnimator?.invalidate()
                attachedStartAnimator?.invalidate()
                attachedPriceFadeAnimation?.invalidate()
            }
        }
    }

    private fun animateStar(holder: StockViewHolder) {
        val scaleInOut =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)
        scaleInOut.setTarget(holder.binding.imageViewFavourite)

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

        holder.attachedPriceAnimator = scaleInOutPrice
        holder.attachedProfitAnimator = scaleInOutProfit

        scaleInOutPrice.setTarget(holder.binding.textViewCurrentPrice)
        scaleInOutProfit.setTarget(holder.binding.textViewDayProfit)

        scaleInOutPrice.start()
        scaleInOutProfit.start()
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
}