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

package com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter

/**
 * [MenuItem] represents a model for adapter which is set to bottom menu
 */
data class MenuItem(
    val id: Int,
    val type: MenuItemType,
    val iconResource: Int,
    val title: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other is MenuItem) {
            return other.id == id
        } else false
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode()
    }
}