package com.ferelin.remote.api.throttleManager

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

interface ThrottleManager {

    /**
     * Provides ability to add request to throttle manager.
     * @param companyOwnerSymbol is a company-owner of request
     * @param apiTag is a key of request that must be invoked
     * @param messageNumber this is the position of UI according to which it is decided whether to
     *  execute the request or not
     * @param eraseIfNotActual is a parameter by which the [ThrottleManagerImpl] deletes the
     *  request if it is not relevant
     * @param ignoreDuplicates is a parameter by which the [ThrottleManagerImpl] deletes the request
     * if it is duplicate
     * */
    fun addRequestToOrder(
        companyOwnerSymbol: String,
        apiTag: String,
        messageNumber: Int = 0,
        eraseIfNotActual: Boolean = true,
        ignoreDuplicates: Boolean = false
    )

    /**
     * Provides ability to add API to invoke requests.
     * @param apiTag is a key by which it will be determined which method to use for invoked request
     * @param onResponse gives a symbol-owner of request that has been invoked and received the result
     * */
    fun setUpApi(apiTag: String, onResponse: (String) -> Unit)

    fun invalidate()
}