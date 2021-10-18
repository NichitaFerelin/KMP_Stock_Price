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

package com.ferelin.firebase.utils

import android.util.Log

fun <T> List<T>.itemsNotIn(searchIn: List<T>): List<T> {
    val itemsNotIn = mutableListOf<T>()

    this.forEach { item ->
        var exists = false
        Log.d("TEST", "Search for $item")
        for (searchItem in searchIn) {
            Log.d("TEST", "Compare [$searchItem] and [$item]")
            if (searchItem == item) {
                exists = true
                break
            }
        }

        if (!exists) {
            Log.d("TEST", "Add $item")
            itemsNotIn.add(item)
        }
    }
    return itemsNotIn
}