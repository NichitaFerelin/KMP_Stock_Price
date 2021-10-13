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

package com.ferelin.feature_news.adapter

import com.ferelin.core.adapter.base.createRecyclerAdapter
import com.ferelin.feature_news.databinding.ItemNewsBinding
import com.ferelin.feature_news.viewData.NewsViewData

const val NEWS_VIEW_TYPE = 1

fun createNewsAdapter(
    onItemClick: (NewsViewData) -> Unit
) = createRecyclerAdapter(
    NEWS_VIEW_TYPE,
    ItemNewsBinding::inflate
) { viewBinding, item, _, _ ->

    with(viewBinding) {
        item as NewsViewData

        textViewSource.text = item.source
        textViewDate.text = item.date
        textViewHeadline.text = item.headline
        textViewSummary.text = item.summary
        textViewUrl.text = item.sourceUrl

        textViewUrl.setOnClickListener {
            onItemClick.invoke(item)
        }
    }
}