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
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.messagesSection.addUser.DialogAddUser
import com.ferelin.stockprice.ui.messagesSection.chats.adapter.ChatRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatsViewModel : BaseViewModel() {

    val relationsAdapter = ChatRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateUserRelations() }
            launch { collectSharedRelationUpdates() }
        }
    }

    fun onAddUserResult(arguments: Bundle) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val addedUserLogin = arguments[DialogAddUser.USER_LOGIN_KEY]
            if (addedUserLogin is String && addedUserLogin.isNotEmpty()) {
                viewModelScope.launch(mCoroutineContext.IO) {
                    /*mDataInteractor.createNewRelation(
                        sourceUserLogin = mDataInteractor.userLogin,
                        associatedUserLogin = addedUserLogin
                    )*/
                }
            }
        }
    }

    private suspend fun collectStateUserRelations() {
        mDataInteractor.stateUserChats
            .filter { it is DataNotificator.DataPrepared }
            .collect { notificator ->
                withContext(mCoroutineContext.Main) {
                    relationsAdapter.setData((notificator as DataNotificator.DataPrepared).data!!)
                }
            }
    }

    private suspend fun collectSharedRelationUpdates() {
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