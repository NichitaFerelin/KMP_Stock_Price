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

import android.animation.Animator
import android.view.animation.Animation
import com.ferelin.core.adapter.base.BaseViewHolder
import com.ferelin.core.adapter.base.ViewDataType
import com.ferelin.core.databinding.ItemStockBinding

class StockViewHolder(
    var attachedPriceAnimator: Animator? = null,
    var attachedProfitAnimator: Animator? = null,
    var attachedPriceFadeAnimation: Animation? = null,
    var attachedStartAnimator: Animator? = null,
    binding: ItemStockBinding,
    onBind: (ItemStockBinding, ViewDataType, Int, MutableList<Any>) -> Unit
) : BaseViewHolder<ItemStockBinding>(binding, onBind)