package com.ferelin.remote.base

import com.ferelin.remote.utils.Api

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

/**
 * [BaseResponse] for all networks responses.
 */
class BaseResponse<T>(
    var responseCode: Int? = null,
    var additionalMessage: String? = null,
    var responseData: T? = null
) {
    companion object {
        fun <T> failed(): BaseResponse<T> {
            return BaseResponse(responseCode = Api.RESPONSE_UNDEFINED)
        }

        fun <T> createResponse(responseBody: T?, responseCode: Int): BaseResponse<T> {
            return when {
                responseCode == 429 -> BaseResponse(Api.RESPONSE_LIMIT)
                responseBody == null -> BaseResponse(Api.RESPONSE_NO_DATA)
                responseCode == 200 -> {
                        BaseResponse(
                            responseCode = Api.RESPONSE_OK,
                            additionalMessage = null,
                            responseData = responseBody
                        )
                }
                else -> failed()
            }
        }
    }
}