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

package com.ferelin.stockprice.dataInteractor.helpers.messagesHelper

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.dataMediator.DataMediator
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesHelperImpl @Inject constructor(
    private val mRepository: Repository,
    private val mDataMediator: DataMediator
) : MessagesHelper {

    override suspend fun getMessagesForLogin(
        login: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return mDataMediator.getMessagesStateForLogin(login)
    }

    override suspend fun loadMessagesAssociatedWithLogin(
        associatedLogin: String
    ): AdaptiveMessagesHolder? {
        val repositoryResponse = mRepository
            .getMessagesAssociatedWithSpecifiedUserFromRealtimeDb(
                sourceUserLogin = mDataMediator.loginWorker.userLogin ?: "",
                secondSideUserLogin = associatedLogin
            ).firstOrNull()

        return when (repositoryResponse) {
            null -> null

            is RepositoryResponse.Failed -> {
                mDataMediator.onLoadMessageError()
                null
            }
            is RepositoryResponse.Success -> {
                mDataMediator.onMessagesLoaded(repositoryResponse.data)
                repositoryResponse.data
            }
        }
    }
}