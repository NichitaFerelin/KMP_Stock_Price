package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerFragment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    arguments: Bundle?
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private var mCompanySymbol = arguments?.get(AboutPagerFragment.KEY_COMPANY_SYMBOL).toString()
    private lateinit var mCompany: AdaptiveCompany

    private val mRecyclerAdapter = NewsRecyclerAdapter().apply {
        setOnNewsClickListener { position ->
            onNewsClicked(position)
        }
    }
    val recyclerAdapter: NewsRecyclerAdapter
        get() = mRecyclerAdapter

    private val mEventOpenNewsDetails = MutableSharedFlow<Bundle>()
    val eventOpenNewsDetails: SharedFlow<Bundle>
        get() = mEventOpenNewsDetails

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {

            launch {
                mCompany = mDataInteractor.getCompany(mCompanySymbol)!!
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.setData(mCompany.companyNews)
                }
            }

            launch {
                mDataInteractor.loadCompanyNews(mCompanySymbol).collect {
                    onNewsLoaded(it.companyNews)
                }
            }
        }
    }

    private fun onNewsClicked(position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val args = bundleOf(
                NewsFragment.KEY_HEADLINE to mCompany.companyNews.headlines[position],
                NewsFragment.KEY_SUMMARY to mCompany.companyNews.summaries[position],
                NewsFragment.KEY_DATE to mCompany.companyNews.dates[position],
                NewsFragment.KEY_BROWSER_URL to mCompany.companyNews.browserUrls[position],
                NewsFragment.KEY_IMAGE_URL to mCompany.companyNews.previewImagesUrls[position]
            )
            mEventOpenNewsDetails.emit(args)
        }
    }

    private fun onNewsLoaded(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.Main) {
            mRecyclerAdapter.setData(news)
        }

        /*viewModelScope.launch(mCoroutineContext.IO) {
            if (mCompany.companyNews.ids.isEmpty()) {
                withContext(mCoroutineContext.Main) {

                }
            } else applyNewsUpdates(news)
        }*/ // TODO обновляет по ссылке изза этого никогда не пустой
    }

    private fun applyNewsUpdates(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            var newNewsCursor = 0
            while (news.ids[newNewsCursor] != mCompany.companyNews.ids[0]) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addItem(
                        news.headlines[newNewsCursor],
                        news.dates[newNewsCursor]
                    )
                }
                newNewsCursor++
            }
            mCompany.companyNews = news
        }
    }
}