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

package com.ferelin.stockprice.dataInteractor.workers.messages

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
import com.ferelin.stockprice.dataInteractor.workers.chats.ChatsWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope,
    private val mChatsWorker: ChatsWorker
) : MessagesWorkerStates {

    private var mMessagesHolder = HashMap<String, ArrayList<AdaptiveMessage>>(20)

    private val mStateMessages =
        MutableStateFlow<DataNotificator<ArrayList<AdaptiveMessage>>>(DataNotificator.None())
    override val stateMessages: StateFlow<DataNotificator<ArrayList<AdaptiveMessage>>>
        get() = mStateMessages

    private val mSharedMessagesUpdates = MutableSharedFlow<AdaptiveMessage>()
    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mSharedMessagesUpdates

    private var mMessagesJob: Job? = null
    private var mDataPreparedFor: String? = null

    fun prepareMessagesFor(associatedUserNumber: String) {
        if (mDataPreparedFor == associatedUserNumber) {
            return
        }

        mAppScope.launch {
            invalidatePreviousState()
            mMessagesJob = launch {
                mStateMessages.value = DataNotificator.Loading()
                mDataPreparedFor = associatedUserNumber
                loadMessagesFromLocalCache(associatedUserNumber)
                loadMessagesFromRemoteCache(associatedUserNumber)
            }
        }
    }

    suspend fun sendMessageTo(associatedUserNumber: String, messageText: String) {
        val userNumber = mRepository.getUserNumber()
        if (userNumber.isNotEmpty()) {
            val newMessage = AdaptiveMessage(
                id = mMessagesHolder[associatedUserNumber]!!.lastIndex + 1,
                associatedUserNumber = associatedUserNumber,
                side = MessageSide.Source,
                text = messageText
            )
            mRepository.cacheNewMessageToRealtimeDb(userNumber, newMessage)
        }
    }

    fun onLogOut() {
        invalidatePreviousState()
        mMessagesHolder.clear()
    }

    private suspend fun loadMessagesFromLocalCache(associatedUserNumber: String) {
        if (mMessagesHolder[associatedUserNumber] == null) {
            val localMessagesResponse = mRepository.getMessagesFromLocalDb(associatedUserNumber)
            val newHolderValue = if (localMessagesResponse is RepositoryResponse.Success) {
                ArrayList(localMessagesResponse.data)
            } else arrayListOf()

            mMessagesHolder[associatedUserNumber] = newHolderValue
        }

        mStateMessages.value = DataNotificator.DataPrepared(mMessagesHolder[associatedUserNumber]!!)
    }

    private suspend fun loadMessagesFromRemoteCache(associatedUserNumber: String) {
        val userNumber = mRepository.getUserNumber()
        if (userNumber.isNotEmpty()) {
            mRepository.getMessagesFromRealtimeDb(
                currentUserNumber = userNumber,
                associatedUserNumber = associatedUserNumber
            ).collect { response ->
                if (response is RepositoryResponse.Success
                    && response.data.associatedUserNumber == associatedUserNumber
                    && !mMessagesHolder[associatedUserNumber]!!.contains(response.data)
                ) {
                    onNewItem(response)
                }
            }
        }
    }

    private suspend fun onNewItem(response: RepositoryResponse.Success<AdaptiveMessage>) {
        val cachedContainer = mMessagesHolder[response.data.associatedUserNumber]
        if (cachedContainer != null) {
            cachedContainer.add(response.data)
            mSharedMessagesUpdates.emit(response.data)
            mAppScope.launch { mRepository.cacheMessageToLocalDb(response.data) }
        } else {
            mChatsWorker.createNewChat(response.data.associatedUserNumber)
            mMessagesHolder[response.data.associatedUserNumber] = arrayListOf(response.data)
            mSharedMessagesUpdates.emit(response.data)
            mAppScope.launch { mRepository.cacheMessageToLocalDb(response.data) }
        }
    }

    private fun invalidatePreviousState() {
        mMessagesJob?.cancel()
        mMessagesJob = null
        mStateMessages.value = DataNotificator.None()
    }
}