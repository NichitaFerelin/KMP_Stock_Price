package com.ferelin.stockprice.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.databinding.ItemStockBinding
import com.ferelin.stockprice.utils.StockClickListener

class StocksRecyclerAdapter(
    private var mStockClickListener: StockClickListener? = null
) : RecyclerView.Adapter<StocksRecyclerAdapter.StockViewHolder>() {

    private var mCompanies = ArrayList<AdaptiveCompany>()
    val companies: List<AdaptiveCompany>
        get() = mCompanies.toList()

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
                mStockClickListener?.onStockClicked(mCompanies[holder.adapterPosition])
            }
            binding.imageViewFavourite.setOnClickListener {
                mStockClickListener?.onFavouriteIconClicked(mCompanies[holder.adapterPosition])
            }
            mOnBindCallback?.invoke(holder, mCompanies[position], position)
        }
    }

    override fun getItemCount(): Int {
        return mCompanies.size
    }

    override fun getItemId(position: Int): Long {
        return mCompanies[position].hashCode().toLong()
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    fun setOnStockCLickListener(listener: StockClickListener) {
        mStockClickListener = listener
    }

    fun setOnBindCallback(
        func: (holder: StockViewHolder, company: AdaptiveCompany, position: Int) -> Unit
    ) {
        mOnBindCallback = func
    }

    fun setCompanies(companies: ArrayList<AdaptiveCompany>) {
        mCompanies = companies
        notifyDataSetChanged()
    }

    fun updateCompany(company: AdaptiveCompany, index: Int) {
        mCompanies[index] = company
        notifyItemChanged(index)
    }

    fun addCompany(company: AdaptiveCompany) {
        mCompanies.add(0, company)
        notifyItemInserted(0)
    }

    fun removeCompany(index: Int) {
        mCompanies.removeAt(index)
        notifyItemRemoved(index)
    }

    class StockViewHolder private constructor(
        val binding: ItemStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdaptiveCompany) {
            binding.apply {
                textViewCompanyName.text = item.companyProfile.name
                textViewCompanySymbol.text = item.companyProfile.symbol
                textViewCurrentPrice.text = item.companyDayData.currentPrice
                textViewDayProfit.text = item.companyDayData.profit
                textViewDayProfit.setTextColor(item.companyStyle.dayProfitBackground)
                imageViewFavourite.setImageResource(item.companyStyle.favouriteIconResource)
                root.setCardBackgroundColor(item.companyStyle.holderBackground)

                Glide
                    .with(root)
                    .load(item.companyProfile.logoUrl)
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