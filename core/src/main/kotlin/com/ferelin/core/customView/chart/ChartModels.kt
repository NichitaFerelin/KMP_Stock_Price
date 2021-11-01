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

package com.ferelin.core.customView.chart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [BezierPoint] represents model to build chart
 */
data class BezierPoint(val x1: Float, val y1: Float, val x2: Float, val y2: Float)

/**
 * [Point] represents base point with coords for chart
 * */
@Parcelize
data class Point(var x: Float, var y: Float) : Parcelable

/**
 * [Marker] represents model of chart "advanced" point with data.
 * */
@Parcelize
data class Marker(
    val position: Point = Point(0f, 0f),
    val price: Double,
    val priceStr: String,
    val date: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is Marker) {
            other.price == price && other.date == date
        } else false
    }

    override fun hashCode(): Int {
        var result = price.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}

/**
 * Chart model
 * */
data class ChartPastPrices(
    val prices: List<Double>,
    val pricesStr: List<String>,
    val dates: List<String>
)