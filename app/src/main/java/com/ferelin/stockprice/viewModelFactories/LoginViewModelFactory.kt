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

package com.ferelin.stockprice.viewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.stockprice.ui.messagesSection.chat.ChatViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory @Inject constructor(
    private val mChat: AdaptiveChat?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(mChat!!) as T
            }
            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}