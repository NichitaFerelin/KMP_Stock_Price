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

package com.ferelin.remote.networkApi.requestsLimiter

/**
 * [RequestsLimiter] is an entity that limits the number of requests for api.
 *
 * Explanation:
 *      the application uses a public api, which has a limitation the number of calls per minute.
 *      With the functionality that the application has, this limit can end in a couple
 *      of seconds and no more requests can be made.
 *      This entity solves this problem.
 *
 * Usage:
 * Call [setUpApi] to which the response from server will return in future.
 * Call [addRequestToOrder] to add your request to server
 * */
interface RequestsLimiter {

    /**
     * Provides ability to add request in order.
     *
     * @param companyOwnerSymbol is a company-owner symbol of request
     * @param apiTag is a key of request that must be invoked
     * @param keyPosition is a key-position according to which it is decided whether to
     *  execute the request or not
     * @param eraseIfNotActual is a parameter by which determines whether your request will
     * be deleted if a lot of time has passed since it was added to the queue
     * @param ignoreDuplicates is a parameter by which determines whether your request will
     * be deleted if it is duplicate
     * */
    fun addRequestToOrder(
        companyOwnerSymbol: String,
        apiTag: String,
        keyPosition: Int = 0,
        eraseIfNotActual: Boolean = true,
        ignoreDuplicates: Boolean = false
    )

    /**
     * Provides ability to add API to invoke requests.
     *
     * @param apiTag is a key by which it will be determined which method to use for invoked request
     * @param onResponse gives a symbol-owner of request that has been invoked and received the result
     * */
    fun setUpApi(apiTag: String, onResponse: (String) -> Unit)

    fun invalidate()
}