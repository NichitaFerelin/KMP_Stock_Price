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

package com.ferelin.feature_chart.utils

import com.ferelin.domain.entities.PastPrice

fun extractPrice(str: String): Double? {
    return str.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
}

fun parseMonthFromDate(date: String): String {
    return date.filter { it.isLetter() }
}

fun parseYearFromDate(date: String): String {
    return date.split(" ").getOrNull(2) ?: ""
}

fun List<PastPrice>.sum(from: Int, to: Int): Double {
    var amount = 0.0
    for (index in from..to) {
        amount += this[index].closePrice
    }
    return amount
}