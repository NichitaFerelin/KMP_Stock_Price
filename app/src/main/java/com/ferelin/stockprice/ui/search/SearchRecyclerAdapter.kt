package com.ferelin.stockprice.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.databinding.ItemTickerBinding

class SearchRecyclerAdapter(
    private var mTickerClickListener: ((item: AdaptiveSearchRequest, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<SearchRecyclerAdapter.TickerViewHolder>() {

    private var mSearches = arrayListOf<AdaptiveSearchRequest>()

    // TODO
    private var mPopularSearches = arrayListOf(
        AdaptiveSearchRequest("Apple"),
        AdaptiveSearchRequest("Microsoft Corp"),
        AdaptiveSearchRequest("Amazon.com"),
        AdaptiveSearchRequest("Alphabet"),
        AdaptiveSearchRequest("JD.com"),
        AdaptiveSearchRequest("Tesla"),
        AdaptiveSearchRequest("Facebook"),
        AdaptiveSearchRequest("Telefonaktiebolaget"),
        AdaptiveSearchRequest("NVIDIA"),
        AdaptiveSearchRequest("Beigene"),
        AdaptiveSearchRequest("Intel"),
        AdaptiveSearchRequest("Netflix"),
        AdaptiveSearchRequest("Adobe"),
        AdaptiveSearchRequest("Cisco"),
        AdaptiveSearchRequest("Yandex"),
        AdaptiveSearchRequest("Zoom"),
        AdaptiveSearchRequest("Starbucks"),
        AdaptiveSearchRequest("Charter"),
        AdaptiveSearchRequest("Sanofi"),
        AdaptiveSearchRequest("Amgen"),
        AdaptiveSearchRequest("Pepsi")
    )

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

    fun setData(items: ArrayList<AdaptiveSearchRequest>) {
        mSearches = items
        notifyDataSetChanged()
    }

    fun addItem(item: AdaptiveSearchRequest) {
        mSearches.add(0, item)
        notifyItemInserted(0)
    }

    fun setOnTickerClickListener(func: (item: AdaptiveSearchRequest, position: Int) -> Unit) {
        mTickerClickListener = func
    }

    fun setPopularSearches() {
        mSearches = mPopularSearches
        notifyDataSetChanged()
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