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

package com.ferelin.stockprice.ui.messagesSection.chats.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.stockprice.databinding.ItemRelationBinding

class ChatRecyclerAdapter(
    private var mChatClickListener: ChatClickListener? = null
) : RecyclerView.Adapter<ChatRecyclerAdapter.RelationsViewHolder>() {

    private var mRelations = arrayListOf<AdaptiveChat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelationsViewHolder {
        return RelationsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RelationsViewHolder, position: Int) {
        holder.bind(mRelations[position].associatedUserNumber)
        holder.binding.root.setOnClickListener {
            mChatClickListener?.onRelationClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return mRelations.size
    }

    override fun getItemId(position: Int): Long {
        return mRelations[position].id.toLong()
    }

    fun getRelation(position: Int): AdaptiveChat {
        return mRelations[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<AdaptiveChat>) {
        mRelations = ArrayList(items)
        notifyDataSetChanged()
    }

    fun addItem(chat: AdaptiveChat) {
        mRelations.add(chat)
        notifyItemInserted(mRelations.size - 1)
    }

    fun setOnClickListener(listener: ChatClickListener) {
        mChatClickListener = listener
    }

    class RelationsViewHolder(
        val binding: ItemRelationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(login: String) {
            binding.textViewLogin.text = login
        }

        companion object {
            fun from(parent: ViewGroup): RelationsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemRelationBinding.inflate(inflater, parent, false)
                return RelationsViewHolder(binding)
            }
        }
    }
}