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

package com.ferelin.remote.database.helpers.userHelper

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface UsersHelper {

    fun findUserByLogin(login: String): Flow<Boolean>

    fun findUserById(userId: String): Flow<Boolean>

    /**
     * Registers new user
     * @param userId is a user id that must be cached to cloud database
     * @param login is a user login selected during registration
     * */
    suspend fun tryToRegister(userId: String, login: String): Flow<BaseResponse<Boolean>>
}