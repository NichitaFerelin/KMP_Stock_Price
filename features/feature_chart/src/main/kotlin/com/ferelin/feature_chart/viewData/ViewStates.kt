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

package com.ferelin.feature_chart.viewData

import com.ferelin.domain.entities.StockPrice

sealed class PastPriceLoadState {
    class Loaded(val chartPastPrices: ChartPastPrices) : PastPriceLoadState()
    object Loading : PastPriceLoadState()
    object Error : PastPriceLoadState()
    object None : PastPriceLoadState()
}

sealed class StockPriceLoadState {
    class Loaded(val stockPrice: StockPrice) : StockPriceLoadState()
    object Loading : StockPriceLoadState()
    object None : StockPriceLoadState()
}

enum class ChartViewMode {
    All,
    Year,
    SixMonths,
    Months,
    Weeks,
    Days
}