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

package com.ferelin.feature_login.mapper

import com.ferelin.domain.sources.AuthenticationState
import com.ferelin.feature_login.viewData.AuthProcessingState
import javax.inject.Inject

class AuthStateMapper @Inject constructor() {

    fun map(authenticationState: AuthenticationState): AuthProcessingState {
        return when (authenticationState) {
            AuthenticationState.PhoneProcessing -> {
                AuthProcessingState.Processing(AuthenticationState.PhoneProcessing)
            }
            AuthenticationState.CodeProcessing -> {
                AuthProcessingState.Processing(AuthenticationState.CodeProcessing)
            }
            AuthenticationState.CodeSent -> {
                AuthProcessingState.None(AuthenticationState.CodeSent)
            }
            AuthenticationState.Complete -> {
                AuthProcessingState.Complete
            }
            else -> AuthProcessingState.Error(authenticationState)
        }
    }
}