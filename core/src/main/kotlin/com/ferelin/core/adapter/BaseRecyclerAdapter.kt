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

package com.ferelin.core.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ferelin.core.utils.recycler.BaseRecyclerViewHolder
import com.ferelin.core.utils.recycler.DiffUtilCallback
import com.ferelin.core.utils.recycler.RecyclerAdapterDelegate
import com.ferelin.core.utils.recycler.ViewHolderType
import java.util.*

class BaseRecyclerAdapter(
    vararg delegates: RecyclerAdapterDelegate,
    diffUtilCallback: DiffUtilCallback = DiffUtilCallback(),
) : ListAdapter<ViewHolderType, BaseRecyclerViewHolder>(diffUtilCallback) {

    private val delegates: List<RecyclerAdapterDelegate> = delegates.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder =
        delegates.getOrNull(viewType)?.onCreateViewHolder(parent)
            ?: throw IllegalStateException(getErrorMessage(viewType))

    override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder,
        position: Int
    ) = holder.bind(currentList[position], emptyList())

    override fun onBindViewHolder(
        holder: BaseRecyclerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = holder.bind(currentList[position], payloads)

    override fun getItemViewType(position: Int): Int =
        delegates.indexOfFirst { it.isForValidType(currentList[position]) }

    fun getItemByPosition(position: Int): ViewHolderType? =
        currentList.getOrNull(position)

    fun onMove(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toList()

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(newList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(newList, i, i - 1)
            }
        }

        submitList(newList)
    }

    fun replace(newItems: List<ViewHolderType>) {
        submitList(newItems)
    }

    fun replaceAsNew(newItems: List<ViewHolderType>) {
        submitList(emptyList())
        submitList(newItems)
    }

    private fun getErrorMessage(viewType: Int): String {
        return "No delegate found for viewType: $viewType items: ${currentList.map { it::class.java.simpleName }
            .toSet()} delegates: ${delegates.map { it.getViewHolderTypeName() }}"
    }
}