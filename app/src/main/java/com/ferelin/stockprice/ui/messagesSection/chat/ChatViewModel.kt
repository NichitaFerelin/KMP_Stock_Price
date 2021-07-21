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
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.messagesSection.chat.adapter.MessagesRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(val chat: AdaptiveChat) : BaseViewModel() {

    val messagesAdapter = MessagesRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    val stateMessages: StateFlow<DataNotificator<ArrayList<AdaptiveMessage>>>
        get() = mDataInteractor.stateMessages

    val sharedMessagesUpdates: SharedFlow<AdaptiveMessage>
        get() = mDataInteractor.sharedMessagesHolderUpdates

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.loadMessagesFor(chat.associatedUserNumber)
        }
    }

    override fun onCleared() {
        mDataInteractor.invalidatePreparedMessages()
        super.onCleared()
    }

    fun onSendClicked(text: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (text.isNotEmpty()) {
                mDataInteractor.sendMessageTo(chat.associatedUserNumber, text)
            }
        }
    }
}