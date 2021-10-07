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

package com.ferelin.core.viewData

import android.animation.Animator
import android.view.animation.Animation
import com.ferelin.core.adapter.base.ViewDataType
import com.ferelin.core.adapter.stocks.ITEM_STOCK_TYPE
import com.ferelin.domain.entities.StockPrice

data class StockViewData(
    val id: Int,
    val name: String,
    val ticker: String,
    val logoUrl: String,
    val style: StockStyle,
    var stockPrice: StockPrice? = null,
    var isFavourite: Boolean = false,
    var addedByIndex: Int = 0
) : ViewDataType(ITEM_STOCK_TYPE) {

    override fun equals(other: Any?): Boolean {
        return if (other is StockViewData) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return id
    }

    override fun getUniqueId(): Long {
        return id.toLong()
    }
}