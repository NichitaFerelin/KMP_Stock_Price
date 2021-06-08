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

package com.ferelin.stockprice.common.menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.databinding.ItemMenuBinding

class MenuItemsAdapter(
    private var mMenuItemClickListener: MenuItemClickListener? = null
) : RecyclerView.Adapter<MenuItemsAdapter.MenuItemViewHolder>() {

    private var mMenuItems = emptyList<MenuItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        return MenuItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(mMenuItems[position])
        holder.binding.root.setOnClickListener {
            mMenuItemClickListener?.onMenuItemClicked(mMenuItems[position])
        }
    }

    override fun getItemCount(): Int {
        return mMenuItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<MenuItem>) {
        mMenuItems = list
        notifyDataSetChanged()
    }

    fun onLogOutNotify() {
        notifyItemRemoved(mMenuItems.lastIndex)
        notifyItemInserted(0)
    }

    fun setOnDrawerMenuClickListener(listenerItem: MenuItemClickListener) {
        mMenuItemClickListener = listenerItem
    }

    class MenuItemViewHolder(
        val binding: ItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.imageViewIcon.setImageResource(menuItem.iconResource)
            binding.textViewTitle.text = menuItem.title
        }

        companion object {
            fun from(parent: ViewGroup): MenuItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMenuBinding.inflate(inflater, parent, false)
                return MenuItemViewHolder(binding)
            }
        }
    }
}