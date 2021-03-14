package com.ferelin.stockprice.ui.aboutSection.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.databinding.ItemNewsBinding

class NewsRecyclerAdapter(
    private var mNewsClickListener: ((position: Int) -> Unit)? = null
) : RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>() {

    private var mNewsIds: ArrayList<String> = arrayListOf()
    private var mNewsHeadlines: ArrayList<String> = arrayListOf()
    private var mNewsDates: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(mNewsHeadlines[position], mNewsDates[position])
        holder.itemView.setOnClickListener {
            mNewsClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return mNewsHeadlines.size
    }

    fun addItem(headline: String, date: String) {
        mNewsHeadlines.add(0, headline)
        mNewsDates.add(0, date)
        notifyItemInserted(0)
    }

    fun setData(news: AdaptiveCompanyNews) {
        mNewsHeadlines = ArrayList(news.headlines)
        mNewsDates = ArrayList(news.dates)
        notifyDataSetChanged()
    }

    fun setOnNewsClickListener(func: (position: Int) -> Unit) {
        mNewsClickListener = func
    }

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headline: String, date: String) {
            binding.textViewHeadline.text = headline
            binding.textViewDate.text = date
        }

        companion object {
            fun from(parent: ViewGroup): NewsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemNewsBinding.inflate(inflater, parent, false)
                return NewsViewHolder(binding)
            }
        }
    }
}