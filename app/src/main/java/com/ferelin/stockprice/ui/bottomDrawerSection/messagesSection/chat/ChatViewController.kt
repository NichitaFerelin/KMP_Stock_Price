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

package com.ferelin.stockprice.ui.bottomDrawerSection.messagesSection.chat

import com.ferelin.repository.adaptiveModels.AdaptiveRelation
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentChatBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.messagesSection.chat.adapter.MessagesItemDecoration
import com.ferelin.stockprice.ui.bottomDrawerSection.messagesSection.chat.adapter.MessagesRecyclerAdapter
import com.ferelin.stockprice.utils.showDefaultDialog

class ChatViewController : BaseViewController<ChatViewAnimator, FragmentChatBinding>() {

    override val mViewAnimator = ChatViewAnimator()

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding.recyclerViewMessages.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(
        messagesRecyclerAdapter: MessagesRecyclerAdapter,
        relation: AdaptiveRelation
    ) {
        viewBinding.recyclerViewMessages.apply {
            adapter = messagesRecyclerAdapter
            addItemDecoration(MessagesItemDecoration(context))
        }
        viewBinding.textViewUserLogin.text = relation.associatedUserLogin
    }

    fun onSendClicked() {
        viewBinding.editTextMessage.setText("")
        mViewAnimator.runScaleInOut(viewBinding.imageViewSend)
    }

    fun onError(message: String) {
        showDefaultDialog(context, message)
    }

    fun onNewItem() {
        viewBinding.recyclerViewMessages.smoothScrollToPosition(
            viewBinding.recyclerViewMessages.adapter!!.itemCount
        )
    }
}