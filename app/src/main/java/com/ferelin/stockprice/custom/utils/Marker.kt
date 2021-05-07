package com.ferelin.stockprice.custom.utils

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

data class Marker(
    val position: Point = Point(0f, 0f),
    val price: Double,
    val priceStr: String,
    val date: String
) {
    override fun equals(other: Any?): Boolean {
        return other is Marker && other.date == date && other.price == price
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + priceStr.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}