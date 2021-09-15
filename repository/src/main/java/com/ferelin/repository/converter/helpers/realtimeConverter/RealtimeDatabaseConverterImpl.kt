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

package com.ferelin.repository.converter.helpers.realtimeConverter

import com.ferelin.remote.RESPONSE_END
import com.ferelin.remote.RESPONSE_NO_DATA
import com.ferelin.remote.RESPONSE_OK
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeDatabaseConverterImpl @Inject constructor() : RealtimeDatabaseConverter {

    override fun convertRealtimeResponseToRepositoryResponse(
        response: BaseResponse<String?>?
    ): RepositoryResponse<String> {
        if (response == null) {
            return RepositoryResponse.Failed()
        }

        return when (response.responseCode) {
            RESPONSE_OK -> RepositoryResponse.Success(data = response.responseData!!)
            RESPONSE_END -> RepositoryResponse.Failed(message = RepositoryMessages.End)
            RESPONSE_NO_DATA -> RepositoryResponse.Failed(message = RepositoryMessages.Empty)
            else -> RepositoryResponse.Failed()
        }
    }
}