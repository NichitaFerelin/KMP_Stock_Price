package com.ferelin.stockprice.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.ItemStockBinding
import com.ferelin.stockprice.utils.StocksClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StocksRecyclerAdapter(
    private var mStocksClickListener: StocksClickListener? = null
) : RecyclerView.Adapter<StocksRecyclerAdapter.StockViewHolder>() {

    private var mCompanies = ArrayList<AdaptiveCompany>()

    private var mOnBindCallback: ((
        holder: StockViewHolder,
        company: AdaptiveCompany,
        position: Int
    ) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        return StockViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.apply {
            bind(mCompanies[position])
            itemView.setOnClickListener {
                mStocksClickListener?.onStockClicked(mCompanies[holder.adapterPosition])
            }
            binding.imageViewFavourite.setOnClickListener {
                mStocksClickListener?.onFavouriteIconClicked(mCompanies[holder.adapterPosition])
            }
            mOnBindCallback?.invoke(holder, mCompanies[position], position)
        }
    }

    override fun getItemCount(): Int {
        return mCompanies.size
    }

    fun setOnStocksCLickListener(listener: StocksClickListener) {
        mStocksClickListener = listener
    }

    fun setOnBindCallback(
        func: (holder: StockViewHolder, company: AdaptiveCompany, position: Int) -> Unit
    ) {
        mOnBindCallback = func
    }

    suspend fun setCompanies(companies: ArrayList<AdaptiveCompany>) {
        mCompanies = companies
        withContext(Dispatchers.Main) { notifyDataSetChanged() }
    }

    suspend fun updateCompany(company: AdaptiveCompany) {
        val index = mCompanies.indexOf(company)
        if (index != -1) {
            mCompanies[index] = company
            withContext(Dispatchers.Main) { notifyItemChanged(index) }
        }
    }

    suspend fun addCompany(company: AdaptiveCompany) {
        mCompanies.add(0, company)
        withContext(Dispatchers.Main) { notifyItemInserted(0) }
    }

    suspend fun removeCompany(company: AdaptiveCompany) {
        val index = mCompanies.indexOf(company)
        if (index != -1) {
            mCompanies.removeAt(index)
            withContext(Dispatchers.Main) { notifyItemRemoved(index) }
        }
    }

    class StockViewHolder private constructor(
        val binding: ItemStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdaptiveCompany) {
            binding.apply {
                textViewTicker.text = item.ticker
                textViewTickerName.text = item.name
                textViewLastPrice.text = item.lastPrice
                textViewDynamic.text = item.dayProfitPercents.lastOrNull() ?: ""
                textViewDynamic.setTextColor(
                    item.tickerProfitBackground.lastOrNull() ?: R.color.black
                )
                imageViewFavourite.setImageResource(item.favouriteIconBackground)
                root.setCardBackgroundColor(item.holderBackground)

                Glide
                    .with(root)
                    .load(item.logoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewIcon)
            }
        }

        companion object {
            fun from(parent: ViewGroup): StockViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemStockBinding.inflate(inflater, parent, false)
                return StockViewHolder(binding)
            }
        }
    }
}