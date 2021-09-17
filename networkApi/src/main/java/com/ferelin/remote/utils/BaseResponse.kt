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

package com.ferelin.remote.utils

/**
 * Represents the entity for all responses from the web
 */
class BaseResponse<T>(
    var code: Int = RESPONSE_UNDEFINED,
    var owner: String? = null,
    var data: T? = null
) {
    companion object {

        fun <T> failed(): BaseResponse<T> {
            return BaseResponse(code = RESPONSE_UNDEFINED)
        }

        fun <T> createResponse(
            responseCode: Int,
            responseOwner: String?,
            responseBody: T?
        ): BaseResponse<T> {
            return when {
                responseCode == RESPONSE_LIMIT -> BaseResponse(RESPONSE_LIMIT)
                responseBody == null -> BaseResponse(RESPONSE_NO_DATA)
                responseCode == RESPONSE_OK -> {
                    BaseResponse(
                        code = RESPONSE_OK,
                        owner = responseOwner,
                        data = responseBody
                    )
                }
                else -> failed()
            }
        }
    }
}