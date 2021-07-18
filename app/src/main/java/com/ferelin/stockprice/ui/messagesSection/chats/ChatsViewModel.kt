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

package com.ferelin.stockprice.ui.messagesSection.chats

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.messagesSection.addUser.DialogAddUser
import com.ferelin.stockprice.ui.messagesSection.chats.adapter.ChatRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatsViewModel : BaseViewModel() {

    val relationsAdapter = ChatRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    val stateUserChats: StateFlow<DataNotificator<List<AdaptiveChat>>>
        get() = mDataInteractor.stateUserChats

    val isUserAuthorized: Boolean
        get() = mDataInteractor.stateIsUserAuthenticated.value

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            collectSharedChatsUpdates()
        }
    }

    fun onAddUserResult(arguments: Bundle) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val addedUserLogin = arguments[DialogAddUser.USER_LOGIN_KEY]
            if (addedUserLogin is String && addedUserLogin.isNotEmpty()) {
                mDataInteractor.createNewChat(addedUserLogin)
            }
        }
    }

    private suspend fun collectSharedChatsUpdates() {
        mDataInteractor.sharedUserChatUpdates.collect { notificator ->
            when (notificator) {
                is DataNotificator.ItemRemoved -> {
                    withContext(mCoroutineContext.Main) {
                        relationsAdapter.notifyItemRemoved(notificator.data!!.id)
                    }
                }
                is DataNotificator.NewItemAdded -> {
                    withContext(mCoroutineContext.Main) {
                        relationsAdapter.addItem(notificator.data!!)
                    }
                }
                else -> Unit
            }
        }
    }
}