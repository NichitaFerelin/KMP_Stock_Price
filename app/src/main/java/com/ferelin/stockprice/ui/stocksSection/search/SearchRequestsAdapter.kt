package com.ferelin.stockprice.ui.stocksSection.search

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

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.databinding.ItemTickerBinding

class SearchRequestsAdapter(
    private var mTickerClickListener: ((item: AdaptiveSearchRequest, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<SearchRequestsAdapter.TickerViewHolder>() {

    private var mSearches = arrayListOf<AdaptiveSearchRequest>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TickerViewHolder {
        return TickerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TickerViewHolder, position: Int) {
        holder.bind(mSearches[position])
        holder.itemView.setOnClickListener {
            mTickerClickListener?.invoke(mSearches[position], position)
        }
    }

    override fun getItemCount(): Int = mSearches.size

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return mSearches[position].id.toLong()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<AdaptiveSearchRequest>) {
        mSearches = items
        notifyDataSetChanged()
    }

    fun setOnTickerClickListener(func: (item: AdaptiveSearchRequest, position: Int) -> Unit) {
        mTickerClickListener = func
    }

    class TickerViewHolder private constructor(
        private val binding: ItemTickerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdaptiveSearchRequest) {
            binding.textViewName.text = item.searchText
        }

        companion object {
            fun from(parent: ViewGroup): TickerViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTickerBinding.inflate(inflater, parent, false)
                return TickerViewHolder(binding)
            }
        }
    }
}