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

package com.ferelin.core.utils.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

inline fun <reified T : ViewHolderType, VB : ViewBinding> createRecyclerAdapter(
    noinline inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    noinline onBind: (VB, ViewHolderType, MutableList<Any>?) -> Unit
) = object : RecyclerAdapterDelegate {

    override fun isForValidType(check: ViewHolderType): Boolean {
        return check is T
    }

    override fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerBindingViewHolder<VB> {
        return BaseRecyclerBindingViewHolder(
            binding = inflater(LayoutInflater.from(parent.context), parent, false),
            onBind = onBind
        )
    }

    override fun getViewHolderTypeName(): String {
        return T::class.java.simpleName
    }
}

interface RecyclerAdapterDelegate {

    fun isForValidType(check: ViewHolderType): Boolean

    fun onCreateViewHolder(parent: ViewGroup): BaseRecyclerViewHolder

    fun getViewHolderTypeName(): String
}

abstract class BaseRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: ViewHolderType, payloads: MutableList<Any>?)
}

open class BaseRecyclerBindingViewHolder<VB : ViewBinding>(
    val binding: VB,
    private val onBind: (VB, ViewHolderType, MutableList<Any>?) -> Unit,
) : BaseRecyclerViewHolder(binding.root) {

    override fun bind(item: ViewHolderType, payloads: MutableList<Any>?) {
        onBind(binding, item, payloads)
    }
}

