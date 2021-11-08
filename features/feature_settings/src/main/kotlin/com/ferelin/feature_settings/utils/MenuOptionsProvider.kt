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

package com.ferelin.feature_settings.utils

import android.content.Context
import com.ferelin.core.R
import com.ferelin.feature_settings.viewData.OptionViewData
import javax.inject.Inject

enum class OptionType {
    AUTH,
    CLEAR_DATA,
    SOURCE_CODE
}

class MenuOptionsProvider @Inject constructor(
    private val context: Context
) {
    fun buildMenuOptions(isUserAuthenticated: Boolean): List<OptionViewData> {
        return listOf(
            OptionViewData(
                id = 0,
                type = OptionType.AUTH,
                title = context.getString(R.string.titleAuthorization),
                source = context.getString(
                    if (isUserAuthenticated) {
                        R.string.sourceAuthorized
                    } else R.string.sourceNotAuthorized
                ),
                iconRes = if (isUserAuthenticated) {
                    R.drawable.outline_logout_24
                } else {
                    R.drawable.outline_login_24
                },
                iconContentDescription = if (isUserAuthenticated) {
                    context.getString(R.string.descriptionLogOut)
                } else {
                    context.getString(R.string.descriptionLogIn)
                }
            ),
            OptionViewData(
                id = 1,
                type = OptionType.SOURCE_CODE,
                title = context.getString(R.string.titleSourceCode),
                source = context.getString(R.string.sourceDownload),
                iconRes = R.drawable.outline_file_download_24,
                iconContentDescription = context.getString(R.string.descriptionDownload)
            ),
            OptionViewData(
                id = 2,
                type = OptionType.CLEAR_DATA,
                title = context.getString(R.string.titleClearData),
                source = context.getString(R.string.sourceClearData),
                iconRes = R.drawable.outline_delete_24,
                iconContentDescription = context.getString(R.string.descriptionDelete)
            )
        )
    }
}