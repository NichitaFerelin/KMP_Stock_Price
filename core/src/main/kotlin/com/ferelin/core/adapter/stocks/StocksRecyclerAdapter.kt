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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.R
import com.ferelin.core.adapter.base.BaseRecyclerViewHolder
import com.ferelin.core.adapter.base.RecyclerAdapterDelegate
import com.ferelin.core.databinding.ItemStockBinding
import com.ferelin.core.viewData.StockViewData
import timber.log.Timber

const val STOCK_VIEW_TYPE = 1

const val PAYLOAD_FAVOURITE_UPDATED = 1
const val PAYLOAD_PRICE_UPDATED = 2

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

                fun ItemStockBinding.setCompanyInfo() {
                    textViewCompanyName.text = item.name
                    textViewCompanySymbol.text = item.ticker
                }

                fun ItemStockBinding.setFavourite() {
                    imageViewFavourite.setImageResource(item.style.favouriteBackgroundIconResource)
                    imageViewBoundedIcon.setImageResource(item.style.favouriteForegroundIconResource)
                }

                fun ItemStockBinding.setCompanyPrice() {
                    textViewCurrentPrice.text = item.stockPrice?.currentPrice ?: ""
                    textViewDayProfit.text = item.stockPrice?.profit ?: ""
                    textViewDayProfit.setTextColor(item.style.dayProfitBackground)
                }

                fun ItemStockBinding.setBackground() {
                    root.setCardBackgroundColor(item.style.holderBackground)
                    root.foreground =
                        ContextCompat.getDrawable(root.context, item.style.rippleForeground)

                    root.setOnClickListener { onStockClick.invoke(item) }
                    imageViewFavourite.setOnClickListener { onFavouriteIconClick.invoke(item) }

                    Glide
                        .with(root)
                        .load(item.logoUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(
                            AppCompatResources.getDrawable(
                                rootLayout.context,
                                R.drawable.ic_load_error
                            )
                        )
                        .into(imageViewIcon)
                }

                with(viewBinding) {
                    if (payloads.isEmpty()) {
                        setCompanyInfo()
                        setCompanyPrice()
                        setFavourite()
                        setBackground()
                    } else {
                        when (payloads[0]) {
                            PAYLOAD_FAVOURITE_UPDATED -> setFavourite()
                            PAYLOAD_PRICE_UPDATED -> setCompanyPrice()
                        }
                    }
                }
            }
        )
    }
}