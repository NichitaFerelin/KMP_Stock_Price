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

import com.ferelin.core.utils.recycler.ViewHolderType
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
) : ViewHolderType {

    override fun getUniqueId(): Long {
        return id.toLong()
    }

    override fun isValidType(other: ViewHolderType): Boolean {
        return other is StockViewData
    }
}