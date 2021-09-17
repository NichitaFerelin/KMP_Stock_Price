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

package com.ferelin.repository.useCaseModels

data class StockPrice(
    var currentPrice: String,
    var previousClosePrice: String,
    var openPrice: String,
    var highPrice: String,
    var lowPrice: String,
    var profit: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is StockPrice) {
            other.currentPrice == currentPrice
        } else false
    }

    override fun hashCode(): Int {
        return currentPrice.hashCode()
    }
}