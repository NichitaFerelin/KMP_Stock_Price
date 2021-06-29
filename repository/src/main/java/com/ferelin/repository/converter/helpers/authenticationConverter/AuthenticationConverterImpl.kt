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

package com.ferelin.repository.converter.helpers.authenticationConverter

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationConverterImpl @Inject constructor() : AuthenticationConverter {

    override fun convertTryToRegisterResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<Boolean> {
        return when (response.responseCode) {
            Api.RESPONSE_OK -> RepositoryResponse.Success(data = true)
            Api.RESPONSE_LOGIN_EXISTS -> RepositoryResponse.Failed(message = RepositoryMessages.AlreadyExists)
            Api.RESPONSE_BAD_LOGIN -> RepositoryResponse.Failed(message = RepositoryMessages.BadLogin)
            else -> RepositoryResponse.Failed()
        }
    }

    override fun convertAuthenticationResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<RepositoryMessages> {
        return when (response.responseCode) {
            Api.VERIFICATION_COMPLETED -> RepositoryResponse.Success(data = RepositoryMessages.Ok)
            Api.VERIFICATION_CODE_SENT -> RepositoryResponse.Success(data = RepositoryMessages.CodeSent)
            Api.VERIFICATION_TOO_MANY_REQUESTS -> RepositoryResponse.Failed(message = RepositoryMessages.Limit)
            else -> RepositoryResponse.Failed()
        }
    }
}