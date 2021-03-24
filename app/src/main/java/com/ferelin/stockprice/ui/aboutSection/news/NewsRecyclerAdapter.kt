package com.ferelin.stockprice.ui.aboutSection.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.stockprice.databinding.ItemNewsBinding

class NewsRecyclerAdapter(
    private var mNewsClickListener: NewsClickListener? = null
) : RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder>() {

    private var mNewsIds: ArrayList<String> = arrayListOf()
    val ids: ArrayList<String>
        get() = mNewsIds

    private var mNewsHeadlines: ArrayList<String> = arrayListOf()
    private var mNewsSummaries: ArrayList<String> = arrayListOf()
    private var mNewsDates: ArrayList<String> = arrayListOf()
    private var mNewsSources: ArrayList<String> = arrayListOf()
    private var mNewsUrls: ArrayList<String> = arrayListOf()

    val dataSize: Int
        get() = mNewsIds.size

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

    fun addItemToStart(news: AdaptiveCompanyNews, position: Int) {
        mNewsIds.add(0, news.ids[position])
        mNewsHeadlines.add(0, news.headlines[position])
        mNewsSummaries.add(0, news.summaries[position])
        mNewsDates.add(0, news.dates[position])
        mNewsSources.add(0, news.sources[position])
        mNewsUrls.add(0, news.browserUrls[position])
        notifyItemInserted(0)
    }

    fun addItemToEnd(news: AdaptiveCompanyNews, position: Int) {
        mNewsIds.add(news.ids[position])
        mNewsHeadlines.add(news.headlines[position])
        mNewsSummaries.add(news.summaries[position])
        mNewsDates.add(news.dates[position])
        mNewsSources.add(news.sources[position])
        mNewsUrls.add(news.browserUrls[position])
        notifyItemInserted(mNewsIds.lastIndex)
    }

    fun setDataInRange(news: AdaptiveCompanyNews, start: Int, end: Int) {
        mNewsIds = ArrayList(news.ids)
        mNewsHeadlines = ArrayList(news.headlines)
        mNewsSummaries = ArrayList(news.summaries)
        mNewsDates = ArrayList(news.dates)
        mNewsSources = ArrayList(news.sources)
        mNewsUrls = ArrayList(news.browserUrls)
        notifyItemRangeInserted(start, end)
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