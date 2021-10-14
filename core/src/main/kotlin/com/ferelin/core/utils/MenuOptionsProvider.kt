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

package com.ferelin.core.utils

import android.content.Context
import com.ferelin.core.R
import com.ferelin.core.viewData.OptionViewData
import javax.inject.Inject

enum class OptionType {
    AUTH,
    CLEAR_DATA
}

class MenuOptionsProvider @Inject constructor(
    private val mContext: Context
) {
    fun buildMenuOptions(isUserAuthenticated: Boolean): List<OptionViewData> {
        return listOf(
            OptionViewData(
                id = 0,
                type = OptionType.AUTH,
                title = mContext.getString(R.string.titleAuthorization),
                source = mContext.getString(
                    if (isUserAuthenticated) {
                        R.string.sourceAuthorized
                    } else R.string.sourceNotAuthorized
                ),
                iconRes = if (isUserAuthenticated) R.drawable.ic_logout else R.drawable.ic_login
            ),
            OptionViewData(
                id = 1,
                type = OptionType.CLEAR_DATA,
                title = mContext.getString(R.string.titleClearData),
                source = mContext.getString(R.string.sourceClearData),
                iconRes = R.drawable.outline_delete_24
            )
        )
    }
}