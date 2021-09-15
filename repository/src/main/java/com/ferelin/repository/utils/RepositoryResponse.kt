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

package com.ferelin.repository.utils

sealed class RepositoryResponse<out T> {

    class Success<out T>(
        val owner: String? = null,
        val data: T
    ) : RepositoryResponse<T>()

    class Failed<out T>(
        val message: RepositoryMessages = RepositoryMessages.Error,
        val owner: String? = null
    ) : RepositoryResponse<T>()
}