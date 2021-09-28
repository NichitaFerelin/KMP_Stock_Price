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
import com.ferelin.feature_login.viewModel.AuthenticationLoadState
import javax.inject.Inject

class AuthenticationMapper @Inject constructor() {

    fun map(authenticationState: AuthenticationState): AuthenticationLoadState {
        return when (authenticationState) {
            AuthenticationState.Complete -> AuthenticationLoadState.Authenticated
            AuthenticationState.CodeSent -> AuthenticationLoadState.Loading(
                state = authenticationState
            )
            else -> AuthenticationLoadState.Error(
                error = authenticationState
            )
        }
    }
}