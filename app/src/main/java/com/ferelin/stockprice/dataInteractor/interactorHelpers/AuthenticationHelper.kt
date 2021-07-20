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

package com.ferelin.stockprice.dataInteractor.interactorHelpers

import android.app.Activity
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.dataInteractor.workers.authentication.AuthenticationWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.Flow

/**
 * Methods for interacting with authentication via [DataInteractor].
 * @see [AuthenticationWorker] to get info about how methods works
 * */
interface AuthenticationHelper {

    suspend fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<DataNotificator<RepositoryMessages>>

    fun logInWithCode(code: String)

    suspend fun logOut()
}