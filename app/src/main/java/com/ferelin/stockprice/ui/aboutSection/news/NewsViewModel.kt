package com.ferelin.stockprice.ui.aboutSection.news

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseDataViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    selectedCompany: AdaptiveCompany? = null
) : BaseDataViewModel(coroutineContextProvider, dataInteractor) {

    private val mSelectedCompany: AdaptiveCompany? = selectedCompany

    private val mRecyclerAdapter = NewsRecyclerAdapter()
    val recyclerAdapter: NewsRecyclerAdapter
        get() = mRecyclerAdapter

    private val mHasDataForRecycler =
        MutableStateFlow(mSelectedCompany?.companyNews?.summaries?.isNotEmpty() ?: false)
    val hasDataForRecycler: StateFlow<Boolean>
        get() = mHasDataForRecycler

    private val mActionOpenUrl = MutableSharedFlow<Intent>()
    val actionOpenUrl: SharedFlow<Intent>
        get() = mActionOpenUrl

    private val mNotificationNewItems = MutableSharedFlow<Unit>()
    val notificationNewItems: SharedFlow<Unit>
        get() = mNotificationNewItems

    private val mActionShowError = MutableStateFlow("")
    val actionShowError: StateFlow<String>
        get() = mActionShowError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {

            if (mHasDataForRecycler.value) {
                onNewsChanged(mSelectedCompany!!.companyNews)
            }

            launch {
                mDataInteractor.isNetworkAvailableState
                    .filter { it }
                    .take(1)
                    .collect()
            }.join()
            launch {
                mDataInteractor.loadCompanyNews(mSelectedCompany?.companyProfile?.symbol ?: "")
                    .take(1)
                    .collect {
                        onNewsChanged(it.companyNews)
                        mHasDataForRecycler.value = true
                    }
            }
            launch {
                mDataInteractor.loadCompanyNewsErrorShared
                    .filter { it.isNotEmpty() }
                    .collect { mActionShowError.value = it }
            }
        }
    }

    fun onNewsUrlClicked(position: Int) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val url = mSelectedCompany?.companyNews?.browserUrls?.get(position)
            val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
            mActionOpenUrl.emit(intent)
        }
    }

    private fun onNewsChanged(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (mRecyclerAdapter.dataSize == 0 && news.ids.size > 10) {
                setNewsDataWithAnim(news)
            } else if (mRecyclerAdapter.ids.firstOrNull() != news.ids.firstOrNull()) {
                addNewsByOne(news)
            }
        }
    }

    private fun setNewsDataWithAnim(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            for (index in 0 until 5) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addItemToEnd(news, index)
                }
                delay(45)
            }
            withContext(mCoroutineContext.Main) {
                mRecyclerAdapter.setDataInRange(news, 5, news.ids.lastIndex)
            }
        }
    }

    private fun addNewsByOne(news: AdaptiveCompanyNews) {
        viewModelScope.launch(mCoroutineContext.IO) {
            val stopId = news.ids.first()
            val newsCursor = 0
            while (newsCursor < news.ids.size && news.ids[newsCursor] != stopId) {
                withContext(mCoroutineContext.Main) {
                    mRecyclerAdapter.addItemToStart(news, newsCursor)
                }
                delay(50)
            }
            mNotificationNewItems.emit(Unit)
        }
    }
}