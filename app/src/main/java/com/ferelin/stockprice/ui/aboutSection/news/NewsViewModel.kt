package com.ferelin.stockprice.ui.aboutSection.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    ownerCompany: AdaptiveCompany? = null
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mOwnerCompany: AdaptiveCompany? = ownerCompany

    private val mRecyclerAdapter = NewsRecyclerAdapter().apply { setHasStableIds(true) }
    val recyclerAdapter: NewsRecyclerAdapter
        get() = mRecyclerAdapter

    private val mHasDataForRecycler =
        MutableStateFlow(mOwnerCompany?.companyNews?.summaries?.isNotEmpty() ?: false)
    val hasDataForRecycler: StateFlow<Boolean>
        get() = mHasDataForRecycler

    private val mActionOpenNewsDetails = MutableSharedFlow<Bundle>()
    val actionOpenNewsDetails: SharedFlow<Bundle>
        get() = mActionOpenNewsDetails

    private val mActionOpenUrl = MutableSharedFlow<Intent>()
    val actionOpenUrl: SharedFlow<Intent>
        get() = mActionOpenUrl

    private val mNotificationDataLoaded = MutableStateFlow(false)
    val notificationDataLoaded: StateFlow<Boolean>
        get() = mNotificationDataLoaded

    private val mNotificationNewItems = MutableSharedFlow<Unit>()
    val notificationNewItems: SharedFlow<Unit>
        get() = mNotificationNewItems

    private val mActionShowError = MutableSharedFlow<String>()
    val actionShowError: SharedFlow<String>
        get() = mActionShowError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (mOwnerCompany?.companyNews?.ids?.isNotEmpty() == true) {
                onNewsChanged(mOwnerCompany.companyNews)
            }
            launch {
                mDataInteractor.loadCompanyNews(mOwnerCompany?.companyProfile?.symbol ?: "")
                    .collect {
                        onNewsChanged(it.companyNews)
                        mHasDataForRecycler.value = true
                    }
            }
            launch {
                mDataInteractor.loadCompanyNewsErrorState.collect {
                    mActionShowError.emit(it)
                }
            }
        }
    }

    fun onNewsUrlClicked(position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val url = mOwnerCompany?.companyNews?.browserUrls?.get(position)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            mActionOpenUrl.emit(intent)
        }
    }

    fun onNewsClicked(position: Int) {
        /*viewModelScope.launch(mCoroutineContext.IO) {
            mOwnerCompany?.companyNews?.let {
                val arguments = bundleOf(
                    NewsDetailsFragment.HEADLINE_STR_KEY to it.headlines[position],
                    NewsDetailsFragment.SUMMARY_STR_KEY to it.summaries[position],
                    NewsDetailsFragment.DATE_STR_KEY to it.dates[position],
                    NewsDetailsFragment.BROWSER_URL_STR_KEY to it.browserUrls[position],
                    NewsDetailsFragment.IMAGE_URL_STR_KEY to it.previewImagesUrls[position]
                )
                mActionOpenNewsDetails.emit(arguments)
            }
        }*/
    }

    private fun onNewsChanged(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (mRecyclerAdapter.dataSize == 0 && news.ids.size > 10) {
                setNewsDataInRange(news)
            } else if (mRecyclerAdapter.ids != news.ids) {
                addNewItems(news)
            }
        }
    }

    private fun setNewsDataInRange(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            for (index in 0 until 5) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addItemToEnd(news, index)
                }
                delay(45)
            }
            mNotificationDataLoaded.value = true
            withContext(mCoroutineContext.Main) {
                mRecyclerAdapter.setDataInRange(news, 5, news.ids.lastIndex)
            }
        }
    }

    private fun addNewItems(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val stopId = news.ids.first()
            val newsCursor = 0
            while (newsCursor < news.ids.size && news.ids[newsCursor] != stopId) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addItemToStart(news, newsCursor)
                }
            }
            mNotificationNewItems.emit(Unit)
        }
    }
}