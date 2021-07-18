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

package com.ferelin.stockprice.ui.messagesSection.chat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.shared.MessageSide
import com.ferelin.stockprice.databinding.ItemMessageReceiveBinding
import com.ferelin.stockprice.databinding.ItemMessageSendBinding

class MessagesRecyclerAdapter(
    private val mMessagesClickListener: MessageClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessages = arrayListOf<AdaptiveMessage>()

    private companion object {
        const val VIEW_TYPE_RECEIVE = 1
        const val VIEW_TYPE_SEND = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_RECEIVE -> MessageReceiveViewHolder.from(parent)
            else -> MessageSendViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageSendViewHolder -> {
                holder.bind(mMessages[position])
                holder.binding.root.setOnClickListener {
                    mMessagesClickListener?.onMessageClicked(position)
                }
            }
            is MessageReceiveViewHolder -> {
                holder.bind(mMessages[position])
                holder.binding.root.setOnClickListener {
                    mMessagesClickListener?.onMessageClicked(position)
                }
            }
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
        return if (mMessages[position].side is MessageSide.Associated) {
            VIEW_TYPE_RECEIVE
        } else VIEW_TYPE_SEND
    }

    class MessageReceiveViewHolder(
        val binding: ItemMessageReceiveBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: AdaptiveMessage) {
            binding.textViewMessage.text = message.text
        }

        companion object {
            fun from(parent: ViewGroup): MessageReceiveViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageReceiveBinding.inflate(inflater, parent, false)
                return MessageReceiveViewHolder(binding)
            }
        }
    }

    class MessageSendViewHolder(
        val binding: ItemMessageSendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: AdaptiveMessage) {
            binding.textViewMessage.text = message.text
        }

        companion object {
            fun from(parent: ViewGroup): MessageSendViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemMessageSendBinding.inflate(inflater, parent, false)
                return MessageSendViewHolder(binding)
            }
        }
    }
}