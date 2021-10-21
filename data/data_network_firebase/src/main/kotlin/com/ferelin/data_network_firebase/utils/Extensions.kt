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

package com.ferelin.data_network_firebase.utils

fun <T> List<T>.itemsNotIn(param: List<T>): List<T> {
    val itemsNotIn = mutableListOf<T>()

    this.forEach { item ->
        var exists = false
        for (paramItem in param) {
            if (paramItem == item) {
                exists = true
                break
            }
        }

        if (!exists) {
            itemsNotIn.add(item)
        }
    }
    return itemsNotIn
}