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

package com.ferelin.feature_search.adapter

import com.ferelin.core.adapter.base.createRecyclerAdapter
import com.ferelin.feature_search.databinding.ItemTickerBinding
import com.ferelin.feature_search.viewData.SearchViewData

const val TICKER_VIEW_TYPE = 3

fun createTickerAdapter(
    onTickerClick: (SearchViewData) -> Unit
) = createRecyclerAdapter(
    TICKER_VIEW_TYPE,
    ItemTickerBinding::inflate
) { viewBinding, item, _, _ ->

    item as SearchViewData

    viewBinding.textViewName.text = item.text
    viewBinding.root.setOnClickListener { onTickerClick.invoke(item) }
}