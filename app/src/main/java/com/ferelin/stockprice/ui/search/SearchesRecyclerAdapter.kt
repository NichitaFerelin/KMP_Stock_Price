package com.ferelin.stockprice.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveSearch
import com.ferelin.stockprice.databinding.ItemTickerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchesRecyclerAdapter(
    private var mTickerClickListener: ((item: AdaptiveSearch, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<SearchesRecyclerAdapter.TickerViewHolder>() {

    private var mSearches = ArrayList<AdaptiveSearch>()

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

    override fun getItemCount(): Int {
        return mSearches.size
    }

    suspend fun setData(items: ArrayList<AdaptiveSearch>) {
        mSearches = items
        withContext(Dispatchers.Main) { notifyDataSetChanged() }
    }

    suspend fun addItem(item: AdaptiveSearch) {
        mSearches.add(0, item)
        withContext(Dispatchers.Main) { notifyItemInserted(0) }
    }

    fun setOnTickerClickListener(func: (item: AdaptiveSearch, position: Int) -> Unit) {
        mTickerClickListener = func
    }

    class TickerViewHolder private constructor(
        private val binding: ItemTickerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdaptiveSearch) {
            binding.textViewName.text = item.tickerName
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