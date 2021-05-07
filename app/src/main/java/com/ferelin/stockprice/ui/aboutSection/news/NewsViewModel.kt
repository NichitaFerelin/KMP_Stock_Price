package com.ferelin.stockprice.ui.aboutSection.news

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor,
    selectedCompany: AdaptiveCompany? = null
) : BaseViewModel(coroutineContextProvider, dataInteractor) {

    private val mSelectedCompany: AdaptiveCompany? = selectedCompany
    val selectedCompany: AdaptiveCompany
        get() = mSelectedCompany!!

    private val mNewsRecyclerAdapter = NewsRecyclerAdapter()
    val newsRecyclerAdapter: NewsRecyclerAdapter
        get() = mNewsRecyclerAdapter

    private val mStateIsNetworkResponded = MutableStateFlow(false)

    private val mStateNews = MutableStateFlow<AdaptiveCompanyNews?>(null)
    val stateNews: StateFlow<AdaptiveCompanyNews?>
        get() = mStateNews

    private val mStateIsDataLoading = MutableStateFlow(false)
    val stateIsDataLoading: StateFlow<Boolean>
        get() = mStateIsDataLoading

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadCompanyNewsError

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            collectStateIsNetworkAvailable()
        }
    }

    private suspend fun collectStateIsNetworkAvailable() {
        mDataInteractor.stateIsNetworkAvailable.collect { isAvailable ->
            if (isAvailable && !mStateIsNetworkResponded.value) {
                mStateIsDataLoading.value = true
                collectCompanyNews()
            } else mStateIsDataLoading.value = false
        }
    }

    private suspend fun collectCompanyNews() {
        val selectedCompanySymbol = mSelectedCompany!!.companyProfile.symbol
        mDataInteractor.loadCompanyNews(selectedCompanySymbol).collect { company ->
            onNewsLoaded(company)
        }
    }

    private fun onNewsLoaded(company: AdaptiveCompany) {
        mStateIsDataLoading.value = false
        mStateIsNetworkResponded.value = true
        mStateNews.value = company.companyNews
    }
}