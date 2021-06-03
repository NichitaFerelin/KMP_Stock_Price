package com.ferelin.remote.api.stockCandles

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

import com.squareup.moshi.Json

class StockCandlesResponse(
    @Json(name = "o") val openPrices: List<Double>,
    @Json(name = "h") val highPrices: List<Double>,
    @Json(name = "l") val lowPrices: List<Double>,
    @Json(name = "c") val closePrices: List<Double>,
    @Json(name = "v") val volumeData: List<Double>,
    @Json(name = "t") val timestamps: List<Long>,
    @Json(name = "s") val responseStatus: String
)