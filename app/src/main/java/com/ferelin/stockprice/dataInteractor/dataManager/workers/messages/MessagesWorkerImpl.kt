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

package com.ferelin.stockprice.dataInteractor.dataManager.workers.messages

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesWorkerImpl @Inject constructor(
    private val mRepository: Repository,
) : MessagesWorker, MessagesWorkerStates {
    private var mMessagesHolders: LinkedHashMap<String, AdaptiveMessagesHolder> = LinkedHashMap(20)

    private val mStateMessages =
        MutableStateFlow<DataNotificator<AdaptiveMessagesHolder>>(DataNotificator.Loading())

    private val mSharedMessagesUpdates = MutableSharedFlow<AdaptiveMessage>()
    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mSharedMessagesUpdates

    override fun getMessagesStateForLogin(
        associatedUserLogin: String,
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return when {
            mStateMessages.value is DataNotificator.DataPrepared
                    && mStateMessages.value.data?.secondSideLogin == associatedUserLogin -> {
                mStateMessages.asStateFlow()
            }

            mMessagesHolders[associatedUserLogin] != null -> {
                mStateMessages.value =
                    DataNotificator.DataPrepared(mMessagesHolders[associatedUserLogin]!!)
                mStateMessages.asStateFlow()
            }

            else -> {
                mStateMessages.value = DataNotificator.NoData()
                mStateMessages.asStateFlow()
            }
        }
    }

    override fun onMessagesLoaded(data: AdaptiveMessagesHolder) {
        mMessagesHolders[data.secondSideLogin] = data
        mStateMessages.value = DataNotificator.DataPrepared(data)
    }

    override fun onNewMessage(
        associatedUserLogin: String,
        message: AdaptiveMessage
    ) {
        when {
            mMessagesHolders[associatedUserLogin] == null -> {
                mMessagesHolders[associatedUserLogin] = AdaptiveMessagesHolder(
                    id = 0, // TODO
                    secondSideLogin = associatedUserLogin,
                    messages = mutableListOf(message)
                )
            }
        }
        // on new relation
    }
}