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

import android.content.Context
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItemType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemsSource @Inject constructor(context: Context) {

    val logInItem = MenuItem(
        id = 0,
        type = MenuItemType.LogIn,
        iconResource = R.drawable.ic_login,
        title = context.getString(R.string.menuLogIn)
    )

    val logOutItem = MenuItem(
        id = 1,
        type = MenuItemType.LogOut,
        iconResource = R.drawable.ic_logout,
        title = context.getString(R.string.menuLogOut)
    )

    val stocksItem = MenuItem(
        id = 2,
        type = MenuItemType.Stocks,
        iconResource = R.drawable.ic_stocks,
        title = context.getString(R.string.menuStocks),
    )

    val notesItem = MenuItem(
        id = 3,
        type = MenuItemType.Notes,
        iconResource = R.drawable.ic_note,
        title = context.getString(R.string.menuNotes)
    )

    val chatsItem = MenuItem(
        id = 4,
        type = MenuItemType.Chats,
        iconResource = R.drawable.ic_message,
        title = context.getString(R.string.menuChats)
    )

    val settingsItem = MenuItem(
        id = 5,
        type = MenuItemType.Settings,
        iconResource = R.drawable.ic_settings,
        title = context.getString(R.string.menuSettings)
    )
}