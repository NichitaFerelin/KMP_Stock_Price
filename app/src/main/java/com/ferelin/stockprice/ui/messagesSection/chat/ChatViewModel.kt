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

package com.ferelin.stockprice.ui.messagesSection.chat

import androidx.lifecycle.viewModelScope
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.messagesSection.chat.adapter.MessagesRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(val associatedUserLogin: String) : BaseViewModel() {

    val messagesAdapter = MessagesRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadMessagesError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateMessages() }
            launch { collectSharedMessagesUpdates() }
        }
    }

    fun onSendClicked(text: String) {
        mAppScope.launch {
            mDataInteractor.sendNewMessage(associatedUserLogin, text)
        }
    }

    private suspend fun collectStateMessages() {
        mDataInteractor.getMessagesStateForLogin(associatedUserLogin).collect { notificator ->
            when (notificator) {
                is DataNotificator.DataPrepared -> {
                    withContext(mCoroutineContext.Main) {
                        messagesAdapter.setData(notificator.data!!.messages)
                    }
                }
                is DataNotificator.NoData -> {
                    mDataInteractor.loadMessagesAssociatedWithLogin(associatedUserLogin)
                }
                else -> Unit
            }
        }
    }

    private suspend fun collectSharedMessagesUpdates() {
        mDataInteractor.sharedMessagesHolderUpdates.collect { adaptiveMessage ->
            withContext(mCoroutineContext.Main) {
                messagesAdapter.addItem(adaptiveMessage)
            }
        }
    }
}