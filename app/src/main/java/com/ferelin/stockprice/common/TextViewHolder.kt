package com.ferelin.stockprice.common

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