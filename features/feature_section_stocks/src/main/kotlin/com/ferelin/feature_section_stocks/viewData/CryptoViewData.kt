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

package com.ferelin.feature_section_stocks.viewData

import androidx.annotation.ColorRes
import com.ferelin.core.adapter.base.ViewDataType
import com.ferelin.feature_section_stocks.adapter.crypto.CRYPTO_VIEW_TYPE

data class CryptoViewData(
    val id: Int,
    val name: String,
    val logoUrl: String,
    val price: String,
    val profit: String,
    @ColorRes val profitColor: Int
) : ViewDataType(CRYPTO_VIEW_TYPE) {

    override fun getUniqueId(): Long {
        return id.toLong()
    }
}