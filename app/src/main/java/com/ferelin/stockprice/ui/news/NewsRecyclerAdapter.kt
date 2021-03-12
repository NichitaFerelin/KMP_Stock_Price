package com.ferelin.stockprice.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.databinding.ItemNewsBinding

class NewsRecyclerAdapter(
    private var mNewsClickListener: ((item: AdaptiveCompanyNews, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>() {

    private var mNews: ArrayList<AdaptiveCompanyNews> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(mNews[position])
        holder.itemView.setOnClickListener {
            mNewsClickListener?.invoke(mNews[position], position)
        }
    }

    override fun getItemCount(): Int {
        return mNews.size
    }

    fun setData(news: ArrayList<AdaptiveCompanyNews>) {
        mNews = news
    }

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdaptiveCompanyNews) {
            //binding.textViewHeadline.text = item.headlines
        }

        companion object {
            fun from(parent: ViewGroup): NewsRecyclerAdapter.NewsViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemNewsBinding.inflate(inflater, parent, false)
                return NewsViewHolder(binding)
            }
        }
    }

}