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
        holder.itemView.setOnClickListener {
            mNewsClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return mNewsHeadlines.size
    }

    override fun getItemId(position: Int): Long {
        return mNewsIds[position].toLong()
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

    fun setData(news: AdaptiveCompanyNews) {
        mNewsIds = ArrayList(news.ids)
        mNewsHeadlines = ArrayList(news.headlines)
        mNewsSummaries = ArrayList(news.summaries)
        mNewsDates = ArrayList(news.dates)
        mNewsSources = ArrayList(news.sources)
        mNewsUrls = ArrayList(news.browserUrls)
        notifyDataSetChanged()
    }

    fun setOnNewsClickListener(func: (position: Int) -> Unit) {
        mNewsClickListener = func
    }

    class NewsViewHolder(
        private val mBinding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(source: String, date: String, headline: String, summary: String, url: String) {
            mBinding.apply {
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