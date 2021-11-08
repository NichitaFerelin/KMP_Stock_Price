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

package com.ferelin.feature_settings.adapter

import com.ferelin.core.adapter.base.createRecyclerAdapter
import com.ferelin.feature_settings.databinding.ItemOptionBinding
import com.ferelin.feature_settings.viewData.OptionViewData

const val OPTION_VIEW_TYPE = 0

fun createOptionsAdapter(
    onOptionClick: (OptionViewData) -> Unit
) = createRecyclerAdapter(
    OPTION_VIEW_TYPE,
    ItemOptionBinding::inflate
) { viewBinding, item, _, _ ->

    item as OptionViewData

    viewBinding.textViewTitle.text = item.title
    viewBinding.textViewSource.text = item.source
    viewBinding.image.setImageResource(item.iconRes)
    viewBinding.image.contentDescription = item.iconContentDescription
    viewBinding.cardHolder.setOnClickListener { onOptionClick.invoke(item) }
}
