package com.ferelin.stockprice.ui.aboutSection.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.databinding.ItemNewsBinding

class NewsRecyclerAdapter(
    private var mNewsClickListener: NewsClickListener? = null
) : RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>() {

    private var mNewsIds: ArrayList<String> = arrayListOf()
    private var mNewsHeadlines: ArrayList<String> = arrayListOf()
    private var mNewsSummaries: ArrayList<String> = arrayListOf()
    private var mNewsDates: ArrayList<String> = arrayListOf()
    private var mNewsSources: ArrayList<String> = arrayListOf()
    private var mNewsUrls: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(
            mNewsSources[position],
            mNewsDates[position],
            mNewsHeadlines[position],
            mNewsSummaries[position],
            mNewsUrls[position]
        )
        holder.binding.textViewUrl.setOnClickListener {
            mNewsClickListener?.onNewsUrlClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return mNewsHeadlines.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(news: AdaptiveCompanyNews) {
        mNewsIds = ArrayList(news.ids)
        mNewsHeadlines = ArrayList(news.headlines)
        mNewsSummaries = ArrayList(news.summaries)
        mNewsDates = ArrayList(news.dates)
        mNewsSources = ArrayList(news.sources)
        mNewsUrls = ArrayList(news.browserUrls)
        notifyDataSetChanged()
    }

    fun setOnNewsClickListener(clickListener: NewsClickListener) {
        mNewsClickListener = clickListener
    }

    class NewsViewHolder(
        val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            source: String,
            date: String,
            headline: String,
            summary: String,
            url: String
        ) {
            binding.apply {
                textViewSource.text = source
                textViewDate.text = date
                textViewHeadline.text = headline
                textViewSummary.text = summary
                textViewUrl.text = url
            }
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