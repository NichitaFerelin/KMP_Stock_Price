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

package com.ferelin.shared

sealed class LoadState<T> {
    class None<T> : LoadState<T>()
    class Prepared<T>(var data: T) : LoadState<T>()
    class Loading<T>(var data: T? = null) : LoadState<T>()
    class Error<T>(var data: T? = null) : LoadState<T>()
}

inline fun <T, R> LoadState<T>.ifPrepared(action: (LoadState.Prepared<T>) -> R?): R? {
    return if (this is LoadState.Prepared) {
        action.invoke(this)
    } else {
        null
    }
}