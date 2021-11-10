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

package com.ferelin.feature_section_stocks.adapter.crypto

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.core.R
import com.ferelin.core.adapter.base.createRecyclerAdapter
import com.ferelin.feature_section_stocks.databinding.ItemCryptoBinding
import com.ferelin.feature_section_stocks.viewData.CryptoViewData

const val CRYPTO_VIEW_TYPE = 1

fun createCryptoAdapter() = createRecyclerAdapter(
    CRYPTO_VIEW_TYPE,
    ItemCryptoBinding::inflate
) { viewBinding, item, _, _ ->

    with(viewBinding) {
        item as CryptoViewData

        textViewCryptoName.text = item.name
        textViewCryptoPrice.text = item.price
        textViewCryptoProfit.text = item.profit

        val context = viewBinding.root.context
        textViewCryptoProfit.setTextColor(
            ContextCompat.getColor(context, item.profitColor)
        )

        Glide
            .with(root)
            .load(item.logoUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_load_error
                )
            )
            .into(imageViewCrypto)
    }
}