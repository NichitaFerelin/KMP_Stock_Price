package com.ferelin.stockprice.ui.stocksSection.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.databinding.ItemStockBinding
import com.ferelin.stockprice.databinding.ItemTextBinding

class StocksRecyclerAdapter(
    private var mStockClickListener: StockClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mCompanies = ArrayList<AdaptiveCompany>()
    val companies: List<AdaptiveCompany>
        get() = mCompanies.toList()

    private var mTextDividers = hashMapOf<Int, String>()

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
            is TextViewHolder -> holder.bind(mTextDividers[position] ?: "")
            is StockViewHolder -> {
                holder.apply {
                    bind(mCompanies[position - mTextDividers.size])
                    itemView.setOnClickListener {
                        mStockClickListener?.onStockClicked(
                            holder,
                            mCompanies[holder.adapterPosition - mTextDividers.size]
                        )
                    }
                    binding.imageViewFavourite.setOnClickListener {
                        mStockClickListener?.onFavouriteIconClicked(mCompanies[holder.adapterPosition - mTextDividers.size])
                    }
                    mOnBindCallback?.invoke(
                        holder,
                        mCompanies[position - mTextDividers.size],
                        position - mTextDividers.size
                    )
                }
            }
            else -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            mTextDividers[position] == null -> ITEM_STOCK_TYPE
            else -> ITEM_TEXT_TYPE
        }
    }

    override fun getItemCount(): Int {
        return mCompanies.size + mTextDividers.size
    }

    override fun getItemId(position: Int): Long {
        return when {
            mTextDividers[position] != null -> mTextDividers[position].hashCode().toLong()
            else -> mCompanies[position - mTextDividers.size].id.toLong()
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

    fun setTextDividers(map: HashMap<Int, String>) {
        mTextDividers = map
        notifyDataSetChanged()
    }

    fun setCompanies(companies: ArrayList<AdaptiveCompany>) {
        mCompanies = companies
        notifyDataSetChanged()
    }

    fun setCompaniesInRange(companies: ArrayList<AdaptiveCompany>, start: Int, end: Int) {
        mCompanies = companies
        notifyItemRangeInserted(start - mTextDividers.size, end - mTextDividers.size)
    }

    fun updateCompany(company: AdaptiveCompany, index: Int) {
        mCompanies[index] = company
        notifyItemChanged(index)
    }

    fun addCompany(company: AdaptiveCompany) {
        mCompanies.add(0, company)
        notifyItemInserted(0 + mTextDividers.size)
    }

    fun addCompanyToEnd(company: AdaptiveCompany) {
        mCompanies.add(company)
        notifyItemInserted(mCompanies.lastIndex - mTextDividers.size)
    }

    fun removeCompany(index: Int) {
        mCompanies.removeAt(index)
        notifyItemRemoved(index - mTextDividers.size)
    }

    fun invalidate() {
        mCompanies.clear()
        notifyDataSetChanged()
    }

    class TextViewHolder private constructor(
        val binding: ItemTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.root.text = text
        }

        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTextBinding.inflate(inflater, parent, false)
                return TextViewHolder(binding)
            }
        }
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
                imageViewFavourite.setImageResource(item.companyStyle.favouriteDefaultIconResource)
                root.setCardBackgroundColor(item.companyStyle.holderBackground)
                root.foreground =
                    ContextCompat.getDrawable(root.context, item.companyStyle.rippleForeground)
                root.transitionName = "root_${item.id}"

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

    companion object {
        const val ITEM_STOCK_TYPE = 0
        const val ITEM_TEXT_TYPE = 1
    }
}