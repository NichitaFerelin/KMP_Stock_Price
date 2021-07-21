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

package com.ferelin.remote.auth

import android.app.Activity
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import kotlinx.coroutines.flow.Flow

/**
 * [AuthenticationManager] provides methods for login / logout by phone number.
 */
interface AuthenticationManager {

    /**
     * @param [holderActivity] is used to build PhoneAuthOptions.
     * @param [phone] phone number to log in.
     * @return [BaseResponse] with [Api] response codes as flow.
     */
    fun tryToLogIn(holderActivity: Activity, phone: String): Flow<BaseResponse<Boolean>>

    /**
     * @param code is a code that was sent to the entered phone number
     */
    fun logInWithCode(code: String)

    fun logOut()

    fun provideUserId(): String?

    fun provideIsUserLogged(): Boolean
}