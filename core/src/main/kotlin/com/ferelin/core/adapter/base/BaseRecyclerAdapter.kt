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

package com.ferelin.core.adapter.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class BaseRecyclerAdapter(
    vararg adapterDelegates: RecyclerAdapterDelegate
) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {

    private var currentList = Collections.synchronizedList(mutableListOf<ViewDataType>())
    private val delegates = adapterDelegates.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val targetIndex = delegates.indexOfFirst { it.itemsViewType == viewType }
        return delegates[targetIndex].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.bind(currentList[position], position, mutableListOf())
    }

    override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.bind(currentList[position], position, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].itemViewType
    }

    override fun getItemId(position: Int): Long {
        return if (hasStableIds()) {
            currentList[position].getUniqueId()
        } else {
            super.getItemId(position)
        }
    }

    fun getByPosition(position: Int): ViewDataType {
        return currentList[position]
    }

    fun getPosition(selector: (ViewDataType) -> Boolean): Int {
        return currentList.indexOfFirst(selector)
    }

    fun update(viewDataType: ViewDataType, position: Int, payloads: Any? = null) {
        currentList[position] = viewDataType
        notifyItemChanged(position, payloads)
    }

    fun add(position: Int, viewDataType: ViewDataType) {
        currentList.add(position, viewDataType)
        notifyItemInserted(position)
    }

    fun removeAt(position: Int) {
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setData(data: List<ViewDataType>) {
        if (currentList.isNotEmpty()) {
            val itemCount = currentList.size
            currentList = mutableListOf()
            notifyItemRangeRemoved(0, itemCount)
        }

        currentList = data.toMutableList()
        notifyItemRangeInserted(0, currentList.size)
    }
}