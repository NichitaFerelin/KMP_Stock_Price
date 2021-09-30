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

package com.ferelin.core.adapter

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.R
import com.ferelin.core.databinding.ItemStockBinding
import com.ferelin.core.utils.recycler.createRecyclerAdapter
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.viewData.StockViewData

const val ITEM_STOCK_TYPE = 0
const val ITEM_TEXT_TYPE = 1

fun createStocksAdapter(
    onStockClick: (StockViewData) -> Unit,
    onFavouriteIconClick: (StockViewData) -> Unit,
    onBindCallback: (StockViewData) -> Unit
) = createRecyclerAdapter<StockViewData, ItemStockBinding>(
    ItemStockBinding::inflate
) { viewBinding, item ->

    item as StockViewData

    onBindCallback.invoke(item)

    with(viewBinding) {
        textViewCompanyName.text = item.name
        textViewCompanySymbol.text = item.ticker
        textViewCurrentPrice.text = item.stockPrice?.currentPrice ?: ""
        textViewDayProfit.text = item.stockPrice?.profit ?: ""
        textViewDayProfit.setTextColor(item.style.dayProfitBackground)
        imageViewFavourite.setImageResource(item.style.favouriteBackgroundIconResource)
        imageViewBoundedIcon.setImageResource(item.style.favouriteForegroundIconResource)

        root.setCardBackgroundColor(item.style.holderBackground)
        root.foreground =
            ContextCompat.getDrawable(root.context, item.style.rippleForeground)


        val context = rootLayout.context
        val errorIcon = AppCompatResources.getDrawable(context, R.drawable.ic_load_error)


        Glide
            .with(root)
            .load(item.logoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(errorIcon)
            .into(imageViewIcon)
    }
}