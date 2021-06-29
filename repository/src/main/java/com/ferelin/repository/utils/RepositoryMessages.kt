package com.ferelin.repository.utils

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

sealed class RepositoryMessages {
    object Ok : RepositoryMessages()
    object End : RepositoryMessages()
    object Empty : RepositoryMessages()
    object Error : RepositoryMessages()
    object Limit : RepositoryMessages()

    // Represent a message that can notify that AuthenticationManagerImpl send code
    object CodeSent : RepositoryMessages()

    // Represent a message that can notify that user choose bad login to register
    object BadLogin : RepositoryMessages()
    object AlreadyExists : RepositoryMessages()
}