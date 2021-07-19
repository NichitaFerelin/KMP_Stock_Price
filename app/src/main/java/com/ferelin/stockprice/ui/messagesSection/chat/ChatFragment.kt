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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentChatBinding
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.viewModelFactories.LoginViewModelFactory
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment(chat: AdaptiveChat? = null) :
    BaseFragment<FragmentChatBinding, ChatViewModel, ChatViewController>() {

    override val mViewController = ChatViewController()
    override val mViewModel: ChatViewModel by viewModels {
        LoginViewModelFactory(chat)
    }

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 200L
        }
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewController.setArgumentsViewDependsOn(
            messagesRecyclerAdapter = mViewModel.messagesAdapter,
            chat = mViewModel.chat
        )
        setUpClickListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateMessages() }
            launch { collectSharedMessagesUpdates() }
            launch { collectEventError() }
        }
    }

    override fun onBackPressedHandle(): Boolean {
        mViewController.onBackPressed()
        return true
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding.run {
            imageViewSend.setOnClickListener {
                mViewModel.onSendClicked(mViewController.viewBinding.editTextMessage.text.toString())
                mViewController.onSendClicked()
            }
            imageViewBack.setOnClickListener {
                mViewController.onBackPressed()
            }
        }
    }

    private suspend fun collectStateMessages() {
        mViewModel.stateMessages.collect { notificator ->
            withContext(mCoroutineContext.Main) {
                when (notificator) {
                    is DataNotificator.DataPrepared -> {
                        mViewController.onDataPrepared(notificator.data!!)
                    }
                    is DataNotificator.Loading -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private suspend fun collectSharedMessagesUpdates() {
        mViewModel.sharedMessagesUpdates.collect { newMessage ->
            withContext(mCoroutineContext.Main) {
                mViewController.onNewMessage(newMessage)
            }
        }
    }

    private suspend fun collectEventError() {
        mViewModel.eventOnError.collect { message ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(message)
            }
        }
    }
}