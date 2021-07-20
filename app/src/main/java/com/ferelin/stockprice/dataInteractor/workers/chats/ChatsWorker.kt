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

package com.ferelin.stockprice.dataInteractor.workers.chats

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ChatsWorker] is an entity for interacting with repository chats methods.
 */
@Singleton
class ChatsWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) : ChatsWorkerStates {

    private var mChats = arrayListOf<AdaptiveChat>()

    private val mStateUserChats =
        MutableStateFlow<DataNotificator<List<AdaptiveChat>>>(DataNotificator.None())
    override val stateUserChats: StateFlow<DataNotificator<List<AdaptiveChat>>>
        get() = mStateUserChats.asStateFlow()

    private val mSharedUserChatsUpdates = MutableSharedFlow<DataNotificator<AdaptiveChat>>()
    override val sharedUserChatUpdates: SharedFlow<DataNotificator<AdaptiveChat>>
        get() = mSharedUserChatsUpdates.asSharedFlow()

    /**
     * To control collecting of new chats
     * */
    private var mChatsJob: Job? = null

    init {
        prepareChats()
    }

    /**
     * Creates new chat.
     * [associatedUserNumber] is an user number with which need to create chat.
     * */
    fun createNewChat(associatedUserNumber: String) {
        mAppScope.launch {
            if (!mRepository.isUserAuthenticated()) {
                return@launch
            }

            // Checks if chat is already exists
            val chatAtContainer = mChats.find { it.associatedUserNumber == associatedUserNumber }
            if (chatAtContainer == null) {
                val newChat = AdaptiveChat(mChats.lastIndex + 1, associatedUserNumber)
                mRepository.cacheChatToRealtimeDb(mRepository.getUserNumber(), newChat)
            }
        }
    }

    fun onLogIn() {
        prepareChats()
    }

    fun onLogOut() {
        mChatsJob?.cancel()
        mChatsJob = null
        mChats.clear()
        mStateUserChats.value = DataNotificator.None()
    }

    private fun prepareChats() {
        mAppScope.launch {
            if (!mRepository.isUserAuthenticated()
                || mStateUserChats.value is DataNotificator.DataPrepared
            ) {
                return@launch
            }

            mChatsJob = launch {
                mStateUserChats.value = DataNotificator.Loading()
                loadItemsFromLocalCache()
                loadItemsFromRemoteCache()
            }
        }
    }

    private suspend fun loadItemsFromLocalCache() {
        mRepository.getAllChatsFromLocalDb().let { cachedChatsResponse ->
            if (cachedChatsResponse is RepositoryResponse.Success) {
                val cachedChats = cachedChatsResponse.data

                val newNotificatorState = if (cachedChats.isNotEmpty()) {
                    mChats.addAll(cachedChats)
                    DataNotificator.DataPrepared(mChats)
                } else DataNotificator.NoData()

                mStateUserChats.value = newNotificatorState
            }
        }
    }

    private suspend fun loadItemsFromRemoteCache() {
        mRepository.getUserChatsFromRealtimeDb(mRepository.getUserNumber()).collect { response ->
            if (response is RepositoryResponse.Success) {
                val remoteChat = response.data

                /*
                * Chat IDs are created in order starting from zero and inserted with the same
                * sorting. If such a chat already exists, then it can be found by the
                * index equal to its id
                * */
                if (mChats.getOrNull(remoteChat.id)?.id != remoteChat.id) {
                    onNewChatReceived(remoteChat)
                }
            }
        }
    }

    private suspend fun onNewChatReceived(newChat: AdaptiveChat) {
        mAppScope.launch { mRepository.cacheChatToLocalDb(newChat) }

        /**
         * If chats container is empty that means that data was not prepared. The main
         * state of chats needs to be updated by DataPrepared state.
         * Otherwise just need to notify about the new item.
         * */
        if (mChats.isEmpty()) {
            mChats.add(newChat)
            mStateUserChats.value = DataNotificator.DataPrepared(mChats)
        } else {
            mChats.add(newChat.id, newChat)
            mSharedUserChatsUpdates.emit(DataNotificator.NewItemAdded(newChat))
        }
    }
}