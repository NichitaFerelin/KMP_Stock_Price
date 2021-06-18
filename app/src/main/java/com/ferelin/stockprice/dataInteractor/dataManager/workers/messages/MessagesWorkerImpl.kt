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
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
import com.ferelin.stockprice.dataInteractor.dataManager.workers.relations.RelationsWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesWorkerImpl @Inject constructor(
    private val mRepository: Repository,
    private val mRelationWorker: RelationsWorker
) : MessagesWorker, MessagesWorkerStates {

    private var mMessagesHolders: LinkedHashMap<String, AdaptiveMessagesHolder> = LinkedHashMap(20)

    private val mStateMessages =
        MutableStateFlow<DataNotificator<AdaptiveMessagesHolder>>(DataNotificator.Loading())

    private val mSharedMessagesUpdates = MutableSharedFlow<AdaptiveMessage>()
    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mSharedMessagesUpdates

    override suspend fun getMessagesStateForLoginFromCache(
        associatedUserLogin: String
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
                val localMessagesHolder = getMessagesFromLocalCache(associatedUserLogin)
                if (localMessagesHolder != null) {
                    mMessagesHolders[localMessagesHolder.secondSideLogin] = localMessagesHolder
                    mStateMessages.value = DataNotificator.DataPrepared(localMessagesHolder)
                } else mStateMessages.value = DataNotificator.NoData()
                mStateMessages.asStateFlow()
            }
        }
    }

    override suspend fun loadMessagesAssociatedWithLogin(
        sourceUserLogin: String,
        associatedLogin: String,
        onError: suspend () -> Unit
    ) {
        val repositoryResponse = mRepository
            .getMessagesAssociatedWithSpecifiedUserFromRealtimeDb(
                sourceUserLogin = sourceUserLogin,
                secondSideUserLogin = associatedLogin
            ).firstOrNull()

        when (repositoryResponse) {
            is RepositoryResponse.Failed -> onError.invoke()
            is RepositoryResponse.Success -> {
                onMessagesLoaded(sourceUserLogin, repositoryResponse.data)
            }
        }
    }

    override suspend fun onNewMessageReceived(
        sourceUserLogin: String,
        associatedUserLogin: String,
        message: AdaptiveMessage,
    ) {

        if (mMessagesHolders[associatedUserLogin] == null) {
            mMessagesHolders[associatedUserLogin] = AdaptiveMessagesHolder(
                id = mMessagesHolders.size,
                secondSideLogin = associatedUserLogin,
                messages = mutableListOf(message)
            )
            mRelationWorker.createNewRelation(sourceUserLogin, associatedUserLogin)
        } else mMessagesHolders[associatedUserLogin]!!.messages.add(message)

        mSharedMessagesUpdates.emit(message)

        mRepository.cacheMessagesHolderToLocalDb(mMessagesHolders[associatedUserLogin]!!)
        mRepository.cacheNewMessageToRealtimeDb(
            sourceUserLogin = sourceUserLogin,
            secondSideUserLogin = associatedUserLogin,
            messageId = message.id.toString(),
            message = message.text,
            side = message.side
        )
    }

    override suspend fun sendNewMessage(
        sourceUserLogin: String,
        associatedUserLogin: String,
        text: String
    ) {
        val newMessage = if (mMessagesHolders[associatedUserLogin] == null) {
            val message = AdaptiveMessage(
                id = 0,
                side = MessageSide.Source,
                text = text
            )
            mMessagesHolders[associatedUserLogin] = AdaptiveMessagesHolder(
                id = mMessagesHolders.size,
                secondSideLogin = associatedUserLogin,
                messages = mutableListOf(message)
            )
            message
        } else {
            AdaptiveMessage(
                id = mMessagesHolders[associatedUserLogin]!!.messages.size,
                side = MessageSide.Source,
                text = text
            )
        }

        mSharedMessagesUpdates.emit(newMessage)

        mRepository.cacheNewMessageToRealtimeDb(
            sourceUserLogin,
            associatedUserLogin,
            newMessage.id.toString(),
            newMessage.text,
            newMessage.side
        )
        mRepository.cacheMessagesHolderToLocalDb(mMessagesHolders[associatedUserLogin]!!)
    }

    private suspend fun onMessagesLoaded(sourceUserLogin: String, data: AdaptiveMessagesHolder) {
        mMessagesHolders[data.secondSideLogin] = data
        mStateMessages.value = DataNotificator.DataPrepared(data)

        mRepository.cacheMessagesHolderToLocalDb(data)
        data.messages.forEach { message ->
            mRepository.cacheNewMessageToRealtimeDb(
                sourceUserLogin = sourceUserLogin,
                secondSideUserLogin = data.secondSideLogin,
                messageId = message.id.toString(),
                message = message.text,
                side = message.side
            )
        }
    }

    private suspend fun getMessagesFromLocalCache(
        associatedUserLogin: String
    ): AdaptiveMessagesHolder? {
        val localResponse = mRepository.getMessagesByLoginFromLocalDb(associatedUserLogin)
        return when {
            localResponse is RepositoryResponse.Success
                    && localResponse.data.messages.isNotEmpty() -> localResponse.data
            else -> null
        }
    }
}