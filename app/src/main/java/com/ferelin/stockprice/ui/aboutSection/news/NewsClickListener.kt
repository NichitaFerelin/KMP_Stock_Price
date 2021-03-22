package com.ferelin.stockprice.ui.aboutSection.news

interface NewsClickListener {
    fun onNewsClicked(
        holder: NewsRecyclerAdapter.NewsViewHolder,
        source: String,
        headline: String,
        summary: String,
        date: String,
        url: String
    )

    fun onNewsUrlClicked(position: Int)
}