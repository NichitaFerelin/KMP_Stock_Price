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

package com.ferelin.stockprice.ui.bottomDrawerSection.messagesSection.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.shared.MessageSide
import com.ferelin.stockprice.databinding.ItemMessageBinding

class MessagesRecyclerAdapter(
    private val mMessagesClickListener: MessageClickListener? = null
) : RecyclerView.Adapter<MessagesRecyclerAdapter.MessageViewHolder>() {

    private var mMessages = arrayListOf<AdaptiveMessage>()

    companion object {
        const val RIGHT_SIDE_KEY = 1
        const val LEFT_SIDE_KEY = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(mMessages[position].text)
        holder.binding.root.setOnClickListener {
            mMessagesClickListener?.onMessageClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun getItemId(position: Int): Long {
        return mMessages[position].id.toLong()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(messages: List<AdaptiveMessage>) {
        mMessages = ArrayList(messages)
        notifyDataSetChanged()
    }

    fun addItem(message: AdaptiveMessage) {
        mMessages.add(message)
        notifyItemInserted(mMessages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessages[position].side is MessageSide.Source) {
            RIGHT_SIDE_KEY
        } else LEFT_SIDE_KEY
    }

    class MessageViewHolder(
        val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.textViewMessage.text = text
        }

        companion object {
            fun from(parent: ViewGroup): MessageViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageBinding.inflate(inflater, parent, false)
                return MessageViewHolder(binding)
            }
        }
    }
}