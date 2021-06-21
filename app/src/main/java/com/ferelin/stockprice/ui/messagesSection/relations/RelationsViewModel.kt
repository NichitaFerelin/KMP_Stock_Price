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

package com.ferelin.stockprice.ui.messagesSection.relations

import androidx.lifecycle.viewModelScope
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.messagesSection.relations.adapter.RelationsRecyclerAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RelationsViewModel : BaseViewModel() {

    private val mUserRegisterState = MutableStateFlow<Boolean?>(null)
    val userRegisterState: StateFlow<Boolean?>
        get() = mUserRegisterState

    val relationsAdapter = RelationsRecyclerAdapter().apply {
        setHasStableIds(true)
    }

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateUserRelations() }
            launch { collectSharedRelationUpdates() }
            launch { getUserRegisterState() }
        }
    }

    private suspend fun collectStateUserRelations() {
        mDataInteractor.stateUserRelations
            .filter { it is DataNotificator.DataPrepared }
            .collect { notificator ->
                withContext(mCoroutineContext.Main) {
                    relationsAdapter.setData((notificator as DataNotificator.DataPrepared).data!!)
                }
            }
    }

    private suspend fun collectSharedRelationUpdates() {
        mDataInteractor.sharedUserRelationsUpdates.collect { notificator ->
            when (notificator) {
                is DataNotificator.ItemRemoved -> {
                    withContext(mCoroutineContext.Main) {
                        relationsAdapter.notifyItemRemoved(notificator.data!!.id)
                    }
                }
                is DataNotificator.NewItemAdded -> {
                    withContext(mCoroutineContext.Main) {
                        relationsAdapter.notifyItemInserted(notificator.data!!.id)
                    }
                }
                else -> Unit
            }
        }
    }

    private suspend fun getUserRegisterState() {
        val isUserRegistered = mDataInteractor.isUserRegistered()
        mUserRegisterState.value = isUserRegistered
    }
}