package com.ferelin.stockprice.ui.stocksSection.common

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
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.common.TextViewHolder

class StocksRecyclerAdapter(
    private var mStockClickListener: StockClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCompanies = ArrayList<AdaptiveCompany>()
    val companies: List<AdaptiveCompany>
        get() = mCompanies.toList()

    private var mHeader: String? = null
    private val mOffsetWithHeader: Int
        get() = if (mHeader == null) 0 else 1

    private var mOnBindCallback: ((
        holder: StockViewHolder,
        company: AdaptiveCompany,
        position: Int
    ) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_STOCK_TYPE -> StockViewHolder.from(parent)
            ITEM_TEXT_TYPE -> TextViewHolder.from(parent)
            else -> throw IllegalStateException("Unknown ViewType[$viewType] at StocksRecyclerAdapter.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextViewHolder -> bindTextViewHolder(holder)
            is StockViewHolder -> bindStockViewHolder(holder, position)
            else -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && mHeader != null -> ITEM_TEXT_TYPE
            else -> ITEM_STOCK_TYPE
        }
    }

    override fun getItemCount(): Int {
        return mCompanies.size + mOffsetWithHeader
    }

    override fun getItemId(position: Int): Long {
        return when {
            position == 0 && mHeader != null -> mHeader.hashCode().toLong()
            else -> mCompanies[position - mOffsetWithHeader].id.toLong()
        }
    }

    fun setOnStockCLickListener(listener: StockClickListener) {
        mStockClickListener = listener
    }

    fun setOnBindCallback(
        func: (holder: StockViewHolder, company: AdaptiveCompany, position: Int) -> Unit
    ) {
        mOnBindCallback = func
    }

    fun setHeader(header: String) {
        mHeader = header
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCompanies(companies: ArrayList<AdaptiveCompany>) {
        mCompanies = companies
        notifyDataSetChanged()
    }

    fun notifyUpdated(index: Int) {
        notifyItemChanged(index + mOffsetWithHeader)
    }

    fun addCompany(company: AdaptiveCompany) {
        mCompanies.add(0, company)
        notifyItemInserted(mOffsetWithHeader)
    }

    fun removeCompany(index: Int) {
        mCompanies.removeAt(index)
        notifyItemRemoved(index + mOffsetWithHeader)
    }

    fun getCompanyByAdapterPosition(position: Int): AdaptiveCompany {
        return mCompanies[position - mOffsetWithHeader]
    }

    fun onRebound(view: StockViewHolder) {
        mStockClickListener?.onHolderRebound(view)
    }

    fun onUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean) {
        mStockClickListener?.onHolderUntouched(stockViewHolder, rebounded)
    }

    private fun bindTextViewHolder(holder: TextViewHolder) {
        holder.bind(mHeader!!)
    }

    private fun bindStockViewHolder(holder: StockViewHolder, position: Int) {
        holder.apply {
            bind(mCompanies[position - mOffsetWithHeader])
            itemView.setOnClickListener {
                mStockClickListener?.onStockClicked(
                    holder,
                    mCompanies[holder.adapterPosition - mOffsetWithHeader]
                )
            }
            binding.imageViewFavourite.setOnClickListener {
                mStockClickListener?.onFavouriteIconClicked(mCompanies[holder.adapterPosition - mOffsetWithHeader])
            }
        }

        mOnBindCallback?.invoke(
            holder,
            mCompanies[position - mOffsetWithHeader],
            position - mOffsetWithHeader
        )
    }

    companion object {
        const val ITEM_STOCK_TYPE = 0
        const val ITEM_TEXT_TYPE = 1
    }
}