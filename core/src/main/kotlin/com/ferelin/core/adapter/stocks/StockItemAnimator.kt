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

package com.ferelin.core.adapter.stocks

import android.animation.AnimatorInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.core.R
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.invalidate

class StockItemAnimator : DefaultItemAnimator() {

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        if (newHolder !is StockViewHolder || preInfo !is StockHolderInfo) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }

        dispatchAnimationFinished(newHolder)

        when {
            isFirstTimePriceLoad(preInfo, newHolder) -> animatePriceFadeIn(newHolder)
            isPriceChanged(preInfo, newHolder) -> animatePriceChanges(newHolder)
            isFavouriteStateChanged(preInfo, newHolder) -> animateStar(newHolder)
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
            val priceStr = viewHolder.viewBinding.textViewCurrentPrice.text.toString()
            val profit = viewHolder.viewBinding.textViewDayProfit.text.toString()
            val drawable = viewHolder.viewBinding.imageViewFavourite.drawable
            return StockHolderInfo(priceStr, profit, drawable)
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
        scaleInOut.setTarget(holder.viewBinding.imageViewFavourite)

        holder.attachedStartAnimator = scaleInOut
        scaleInOut.start()
    }

    private fun animatePriceFadeIn(holder: StockViewHolder) {
        val fadeIn = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        fadeIn.setAnimationListener(object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                holder.viewBinding.textViewCurrentPrice.alpha = 1F
                holder.viewBinding.textViewDayProfit.alpha = 1F
                fadeIn.setAnimationListener(null)
            }
        })
        holder.attachedPriceFadeAnimation = fadeIn
        holder.viewBinding.textViewCurrentPrice.startAnimation(fadeIn)
        holder.viewBinding.textViewDayProfit.startAnimation(fadeIn)
    }

    private fun animatePriceChanges(holder: StockViewHolder) {
        val scaleInOutPrice =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)
        val scaleInOutProfit =
            AnimatorInflater.loadAnimator(holder.itemView.context, R.animator.scale_in_out)

        holder.attachedPriceAnimator = scaleInOutPrice
        holder.attachedProfitAnimator = scaleInOutProfit

        scaleInOutPrice.setTarget(holder.viewBinding.textViewCurrentPrice)
        scaleInOutProfit.setTarget(holder.viewBinding.textViewDayProfit)

        scaleInOutPrice.start()
        scaleInOutProfit.start()
    }

    private fun isFirstTimePriceLoad(
        preInfo: StockHolderInfo,
        newHolder: StockViewHolder
    ): Boolean {
        return preInfo.price.isEmpty()
                && newHolder.viewBinding.textViewCurrentPrice.text.toString().isNotEmpty()
    }

    private fun isPriceChanged(
        preInfo: StockHolderInfo,
        newHolder: StockViewHolder
    ): Boolean {
        return preInfo.price != newHolder.viewBinding.textViewCurrentPrice.text.toString()
                || preInfo.profit != newHolder.viewBinding.textViewDayProfit.text.toString()
    }

    private fun isFavouriteStateChanged(
        preInfo: StockHolderInfo,
        newHolder: StockViewHolder
    ): Boolean {
        return preInfo.favouriteIcon != newHolder.viewBinding.imageViewFavourite.drawable
    }
}