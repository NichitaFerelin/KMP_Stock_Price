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

package com.ferelin.stockprice.dataInteractor.dataManager.workers

import android.content.Context
import com.ferelin.stockprice.R
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.common.menu.MenuItemType
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MenuItemsWorker @Inject constructor(
    context: Context,
    @Named("isUserLogged") isUserLogged: Boolean
) {
    private val mLogInItem = MenuItem(
        id = 0,
        type = MenuItemType.LogIn,
        iconResource = R.drawable.ic_login,
        title = context.getString(R.string.menuLogIn)
    )
    private val mLogOutItem = MenuItem(
        id = 1,
        type = MenuItemType.LogOut,
        iconResource = R.drawable.ic_logout,
        title = context.getString(R.string.menuLogOut)
    )

    private val mMenuItems = mutableListOf(
        MenuItem(
            id = 2,
            type = MenuItemType.Notes,
            iconResource = R.drawable.ic_note,
            title = context.getString(R.string.menuNotes)
        ),
        MenuItem(
            id = 3,
            type = MenuItemType.Messages,
            iconResource = R.drawable.ic_message,
            title = context.getString(R.string.menuMessages)
        ),
        MenuItem(
            id = 4,
            type = MenuItemType.Settings,
            iconResource = R.drawable.ic_settings,
            title = context.getString(R.string.menuSettings)
        ),
    )

    init {
        if (isUserLogged) {
            mMenuItems.add(mLogOutItem)
        } else mMenuItems.add(0, mLogInItem)
    }

    private val mStateMenuItems = MutableStateFlow<DataNotificator<List<MenuItem>>>(
        DataNotificator.DataPrepared(mMenuItems)
    )
    val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mStateMenuItems

    private val mSharedLogOut = MutableSharedFlow<Unit>()
    val sharedLogOut: SharedFlow<Unit>
        get() = mSharedLogOut

    suspend fun onLogStateChanged(isLogged: Boolean) {
        if (isLogged) {
            mMenuItems.remove(mLogInItem)
            mMenuItems.add(mLogOutItem)
        } else {
            mMenuItems.remove(mLogOutItem)
            mMenuItems.add(0, mLogInItem)
            mSharedLogOut.emit(Unit)
        }
    }
}