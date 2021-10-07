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

package com.ferelin.domain.sources

import com.ferelin.domain.interactors.PastPriceState
import com.ferelin.domain.utils.AppDate

sealed class Resolutions(val key: String) {
    object Day : Resolutions("D")
    object Week : Resolutions("W")
    object Year : Resolutions("Y")
}

interface PastPriceSource {

    suspend fun loadPastPrices(
        companyId: Int,
        companyTicker: String,
        from: Long = AppDate.toTimeMillisForRequest(System.currentTimeMillis() - AppDate.ONE_YEAR),
        to: Long = AppDate.toTimeMillisForRequest(System.currentTimeMillis()),
        resolution: String = Resolutions.Day.key
    ): PastPriceState
}