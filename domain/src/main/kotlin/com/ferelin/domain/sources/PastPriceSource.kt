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

import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.utils.AppDate
import com.ferelin.shared.LoadState

// Response format type
sealed class Resolutions(val key: String) {
    object Day : Resolutions("D")
    object Week : Resolutions("W")
    object Year : Resolutions("Y")
}

interface PastPriceSource {

    /**
     * Loads past prices
     * @param companyId is a company id for which need to load past prices
     * @param companyTicker is a company ticker for which need to load past prices
     * @param from is a range start time-border by which need to load past prices
     * @param to is a range end time-border by which need to load past prices
     * @param resolution is a response format type
     * @return [LoadState] with list of past prices
     * */
    suspend fun loadBy(
        companyId: Int,
        companyTicker: String,
        from: Long = AppDate.toTimeMillisForRequest(System.currentTimeMillis() - AppDate.ONE_YEAR),
        to: Long = AppDate.toTimeMillisForRequest(System.currentTimeMillis()),
        resolution: String = Resolutions.Day.key
    ): LoadState<List<PastPrice>>
}