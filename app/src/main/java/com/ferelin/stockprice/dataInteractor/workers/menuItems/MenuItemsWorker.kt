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

package com.ferelin.stockprice.dataInteractor.workers.menuItems

import com.ferelin.repository.Repository
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItem
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemsWorker @Inject constructor(
    private val mMenuItemsSource: MenuItemsSource,
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope,
) : MenuItemsWorkerStates {

    private val mMenuItems = mutableListOf(
        mMenuItemsSource.stocksItem,
        mMenuItemsSource.notesItem,
        mMenuItemsSource.chatsItem,
        mMenuItemsSource.settingsItem
    )

    private val mStateMenuItems = MutableStateFlow<DataNotificator<List<MenuItem>>>(
        DataNotificator.DataPrepared(mMenuItems)
    )
    override val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mStateMenuItems

    init {
        prepareMenu()
    }

    fun onLogIn() {
        mMenuItems.remove(mMenuItemsSource.logInItem)
        mMenuItems.add(mMenuItemsSource.logOutItem)
        mStateMenuItems.value = DataNotificator.DataPrepared(mMenuItems)
    }

    fun onLogOut() {
        mMenuItems.remove(mMenuItemsSource.logOutItem)
        mMenuItems.add(0, mMenuItemsSource.logInItem)
        mStateMenuItems.value = DataNotificator.DataPrepared(mMenuItems)
    }

    private fun prepareMenu() {
        mAppScope.launch {
            if (mRepository.isUserAuthenticated()) {
                mMenuItems.add(mMenuItemsSource.logOutItem)
            } else mMenuItems.add(mMenuItemsSource.logInItem)
        }
    }
}