package com.ferelin.stockprice.ui.stocksSection.common.adapter

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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.stockprice.databinding.ItemTextBinding

class TextViewHolder private constructor(
    private val mBinding: ItemTextBinding
) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(text: String) {
        mBinding.root.text = text
    }

    companion object {
        fun from(parent: ViewGroup): TextViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTextBinding.inflate(inflater, parent, false)
            return TextViewHolder(binding)
        }
    }
}