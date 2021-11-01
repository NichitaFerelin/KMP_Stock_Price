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

import com.ferelin.core.adapter.base.ViewDataType
import com.ferelin.feature_news.adapter.NEWS_VIEW_TYPE

data class NewsViewData(
    val id: Long = 0L,
    val cloudId: String,
    val headline: String,
    val previewImageUrl: String,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val date: String
) : ViewDataType(NEWS_VIEW_TYPE) {

    override fun getUniqueId(): Long {
        return id
    }

    override fun equals(other: Any?): Boolean {
        return if (other is NewsViewData) {
            other.cloudId == cloudId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return cloudId.hashCode()
    }
}