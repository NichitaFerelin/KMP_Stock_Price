package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.aboutSection.newsDetails.NewsDetailsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    ownerCompany: AdaptiveCompany? = null
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mOwnerCompany: AdaptiveCompany? = ownerCompany

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
            if (mOwnerCompany?.companyNews?.ids?.isNotEmpty() == true) {
                onNewsChanged(mOwnerCompany.companyNews)
            }

            mDataInteractor.loadCompanyNews(mOwnerCompany?.companyProfile?.symbol ?: "")
                .collect { onNewsChanged(it.companyNews) }
        }
    }

    private fun onNewsClicked(position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mOwnerCompany?.companyNews?.let {
                val arguments = bundleOf(
                    NewsDetailsFragment.HEADLINE_STR_KEY to it.headlines[position],
                    NewsDetailsFragment.SUMMARY_STR_KEY to it.summaries[position],
                    NewsDetailsFragment.DATE_STR_KEY to it.dates[position],
                    NewsDetailsFragment.BROWSER_URL_STR_KEY to it.browserUrls[position],
                    NewsDetailsFragment.IMAGE_URL_STR_KEY to it.previewImagesUrls[position]
                )
                mEventOpenNewsDetails.emit(arguments)
            }
        }
    }

    private fun onNewsChanged(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (mRecyclerAdapter.dataSize == 0 && news.ids.size > 10) {
                for (index in 0 until 10) {
                    withContext(mCoroutineContext.Main) {
                        mRecyclerAdapter.addItemToEnd(news, index)
                    }
                    delay(30)
                }
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.setDataInRange(news, 10, news.ids.lastIndex)
                }
            } else if (mRecyclerAdapter.ids != news.ids) {
                /*val cursor = 0
                while (mRecyclerAdapter.ids[cursor] != news.ids.first()
                    && cursor < mRecyclerAdapter.ids.size
                    && cursor < news.ids.size
                ) { // TODO

                }*/
            }
        }
    }
}