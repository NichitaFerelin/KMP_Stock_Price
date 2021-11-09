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

package com.ferelin.feature_settings.viewData

import com.ferelin.core.adapter.base.ViewDataType
import com.ferelin.feature_settings.adapter.SWITCH_OPTION_VIEW_TYPE
import com.ferelin.feature_settings.utils.OptionType

data class SwitchOptionViewData(
    val id: Long,
    val type: OptionType,
    val title: String,
    val source: String,
    val isChecked: Boolean
) : ViewDataType(SWITCH_OPTION_VIEW_TYPE) {

    override fun getUniqueId(): Long {
        return id
    }
}