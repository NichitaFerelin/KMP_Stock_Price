package com.ferelin.stockprice.ui.search

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.utils.CoroutineContextProvider
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.search
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    private var mCompanies: List<AdaptiveCompany>? = null

    private val mPopularRequestsAdapter = SearchRecyclerAdapter().apply {
        setOnTickerClickListener { item, position ->

        }
    }
    val popularRequestsAdapter: SearchRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchRecyclerAdapter().apply {
        setOnTickerClickListener { item, position ->

        }
    }
    val searchesAdapter: SearchRecyclerAdapter
        get() = mSearchesAdapter

    private var mActionShowHintsHideResults = MutableStateFlow(true)
    val actionShowHintsHideResults: StateFlow<Boolean>
        get() = mActionShowHintsHideResults

    override fun initObservers() {
        super.initObservers()

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState.collect {
                    onCompaniesStateResponse(it)
                }
            }
            launch {
                mDataInteractor.searchesRequestsState.collect {
                    onSearchRequestsResponse(it)
                }
            }
            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    onFavouriteCompaniesStateUpdate(it)
                }
            }
        }
    }

    fun onSearchTextChanged(editable: Editable?) {
        viewModelScope.launch(mCoroutineContext.IO) {
            editable?.let {
                val searchText = it.toString()
                if (searchText.isNotEmpty()) {
                    val results = mutableListOf<AdaptiveCompany>()
                    mCompanies?.forEach { company ->
                        if (search(company, searchText)) {
                            results.add(company)
                        }
                    }
                    mRecyclerAdapter.invalidate()

                    if (results.isNotEmpty()) {
                        val resultArray = ArrayList(results)
                        switchSectionsVisibility(false)
                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.setCompanies(resultArray)
                        }
                    } else switchSectionsVisibility(true)
                } else switchSectionsVisibility(true)
            }
        }
    }

    private fun onCompaniesStateResponse(response: DataNotificator<List<AdaptiveCompany>>) {
        if (response is DataNotificator.Success) {
            mCompanies = response.data
        }
    }

    private fun onSearchRequestsResponse(response: DataNotificator<List<AdaptiveSearchRequest>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (response is DataNotificator.Success) {
                val array = ArrayList(response.data)
                withContext(mCoroutineContext.Main) {
                    mSearchesAdapter.setData(array)
                }
            }
        }
    }

    private fun onFavouriteCompaniesStateUpdate(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            when (notificator) {
                is DataNotificator.NewItem -> {
                    val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                    if (index != -1) {
                        mRecyclerAdapter.updateCompany(notificator.data, index)
                    }
                }
                is DataNotificator.Remove -> {
                    val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                    if (index != -1) {
                        mRecyclerAdapter.updateCompany(notificator.data, index)
                    }
                }
                else -> Unit
            }
        }
    }

    private fun switchSectionsVisibility(showHintsHideResults: Boolean) {
        mActionShowHintsHideResults.value = showHintsHideResults
    }
}