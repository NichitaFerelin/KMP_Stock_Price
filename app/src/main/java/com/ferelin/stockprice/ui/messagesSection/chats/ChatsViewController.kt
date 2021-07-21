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
import androidx.fragment.app.Fragment
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentChatsBinding
import com.ferelin.stockprice.ui.messagesSection.addUser.DialogAddUser
import com.ferelin.stockprice.ui.messagesSection.chats.adapter.ChatItemDecoration
import com.ferelin.stockprice.ui.messagesSection.chats.adapter.ChatRecyclerAdapter
import com.ferelin.stockprice.utils.showDefaultDialog
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class ChatsViewController :
    BaseViewController<ChatsViewAnimator, FragmentChatsBinding>() {

    override val mViewAnimator = ChatsViewAnimator()

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        fragment.apply {
            enterTransition = MaterialFadeThrough().apply {
                duration = 200L
            }
            exitTransition = MaterialElevationScale(false).apply {
                duration = 200L
            }
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        viewBinding.recyclerViewRelations.addItemDecoration(ChatItemDecoration(context))
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding.recyclerViewRelations.adapter = null
            super.onDestroyView()
        }
    }

    fun onDataChanged(newData: List<AdaptiveChat>) {
        val adapter = viewBinding.recyclerViewRelations.adapter
        if (adapter is ChatRecyclerAdapter) {
            adapter.setData(newData)
        }
    }

    fun setArgumentsViewDependsOn(adapter: ChatRecyclerAdapter, isUserAuthorized: Boolean) {
        viewBinding.recyclerViewRelations.adapter = adapter

        if (!isUserAuthorized) {
            showDefaultDialog(context, context.getString(R.string.errorNotAuthorized))
        }
    }

    fun onNotificatorNoneState() {
        val adapter = viewBinding.recyclerViewRelations.adapter
        if (adapter is ChatRecyclerAdapter) {
            adapter.setData(emptyList())
        }
    }

    fun onAddPersonClicked(currentFragment: Fragment) {
        mViewAnimator.runScaleInOut(viewBinding.imageViewAdd)
        DialogAddUser().show(currentFragment.parentFragmentManager, null)
    }

    fun onChatClicked(position: Int) {
        val recyclerAdapter = viewBinding.recyclerViewRelations.adapter
        if (recyclerAdapter is ChatRecyclerAdapter) {
            val selectedChat = recyclerAdapter.getChat(position)
            mNavigator?.navigateToChatFragment(selectedChat)
        }
    }
}