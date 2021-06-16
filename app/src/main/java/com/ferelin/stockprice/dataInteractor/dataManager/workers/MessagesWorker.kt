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

package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesEntity
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesWorker @Inject constructor(
    private val mRepository: Repository
) {
    private var mMessagesHolderEntities: HashMap<String, List<AdaptiveMessagesHolder>> = HashMap(10, 0.2F)

    private val mStateMessages =
        MutableStateFlow<DataNotificator<HashMap<String, List<AdaptiveMessagesHolder>>>>(
            DataNotificator.Loading()
        )
    val stateMessagesHolder: StateFlow<DataNotificator<HashMap<String, List<AdaptiveMessagesHolder>>>>
        get() = mStateMessages

    /**
     * Pair [PhoneNumber : Message]
     * */
    private val mSharedMessagesUpdates =
        MutableSharedFlow<DataNotificator<Pair<String, AdaptiveMessagesHolder>>>()
    val sharedMessagesHolderUpdates: SharedFlow<DataNotificator<Pair<String, AdaptiveMessagesHolder>>>
        get() = mSharedMessagesUpdates

    fun onDataPrepared(messagesEntities: List<AdaptiveMessagesEntity>) {
        messagesEntities.forEach { messageEntity ->
            mMessagesHolderEntities[messageEntity.secondSideKey] = messageEntity.messages
        }
        mStateMessages.value = DataNotificator.DataPrepared(mMessagesHolderEntities)
    }

    fun cacheNewMessage() {
        /**
         * TODO
         * */
    }

    suspend fun clearMessages() {
        mMessagesHolderEntities.clear()
        mStateMessages.value = DataNotificator.Loading()
        mRepository.clearSearchesHistory()
    }
}