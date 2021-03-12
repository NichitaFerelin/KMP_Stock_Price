package com.ferelin.stockprice.ui.search

import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.base.BaseStocksViewModel
import com.ferelin.stockprice.dataInteractor.DataInteractor

class SearchViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {
/*
    private var mCompanies: List<AdaptiveCompany>? = null

    private val mPopularRequestsAdapter = SearchRecyclerAdapter()
    val popularRequestsAdapter: SearchRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchRecyclerAdapter()
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
            *//*launch {
                mDataInteractor.searchesRequestsState.collect {
                    onSearchRequestsResponse(it)
                }
            }
            launch {
                mDataInteractor.favouriteCompaniesUpdateState.collect {
                    onFavouriteCompaniesStateUpdate(it)
                }
            }*//*
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
                        if (results.size <= 10) {
                            addToSearched(searchText)
                        }
                    } else switchSectionsVisibility(true)
                } else switchSectionsVisibility(true)
            }
        }
    }

    private fun addToSearched(searchText: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.onNewSearch(searchText)
        }
    }

    private fun onCompaniesStateResponse(response: DataNotificator<List<AdaptiveCompany>>) {
        if (response is DataNotificator.DataPrepared) {
            mCompanies = response.data
        }
    }

    private fun onSearchRequestsResponse(response: DataNotificator<List<AdaptiveSearchRequest>>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            if (response is DataNotificator.DataPrepared) {
                val array = ArrayList(response.data)
                withContext(mCoroutineContext.Main) {
                    mSearchesAdapter.setData(array)
                }
            }
        }
    }
*//*
    private fun onFavouriteCompaniesStateUpdate(notificator: DataNotificator<AdaptiveCompany>) {
        viewModelScope.launch(mCoroutineContext.IO) {
            when (notificator) {
                is DataNotificator.NewItemAdded -> {
                    val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                    if (index != -1) {
                        mRecyclerAdapter.updateCompany(notificator.data, index)
                    }
                }
                is DataNotificator.ItemRemoved -> {
                    val index = mRecyclerAdapter.companies.indexOf(notificator.data)
                    if (index != -1) {
                        mRecyclerAdapter.updateCompany(notificator.data, index)
                    }
                }
                else -> Unit
            }
        }
    }*//*

    private fun switchSectionsVisibility(showHintsHideResults: Boolean) {
        mActionShowHintsHideResults.value = showHintsHideResults
    }*/
}