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

package com.ferelin.feature_news.viewData

import com.ferelin.core.utils.recycler.ViewHolderType

class NewsViewData(
    val id: Long = 0L,
    val relationId: Int = 0,
    val cloudId: String,
    val headline: String,
    val date: String,
    val previewImageUrl: String,
    val source: String,
    val sourceUrl: String,
    val summary: String
) : ViewHolderType {

    override fun getUniqueId(): Long {
        return id
    }

    override fun isValidType(other: ViewHolderType): Boolean {
        return other is NewsViewData
    }
}