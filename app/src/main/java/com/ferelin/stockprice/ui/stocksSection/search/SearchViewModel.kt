package com.ferelin.stockprice.ui.stocksSection.search

import android.text.Editable
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.search
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    coroutineContextProvider: CoroutineContextProvider,
    dataInteractor: DataInteractor
) : BaseStocksViewModel(coroutineContextProvider, dataInteractor) {

    private var mCompanies: List<AdaptiveCompany>? = null
    private var mLastTextSearch = ""

    private val mPopularRequestsAdapter = SearchRecyclerAdapter().apply { setPopularSearches() }
    val popularRequestsAdapter: SearchRecyclerAdapter
        get() = mPopularRequestsAdapter

    private val mSearchesAdapter = SearchRecyclerAdapter()
    val searchesAdapter: SearchRecyclerAdapter
        get() = mSearchesAdapter

    private val mActionHideCloseIcon = MutableStateFlow(true)
    val actionHideCloseIcon: StateFlow<Boolean>
        get() = mActionHideCloseIcon

    private var mActionShowHintsHideResults = MutableStateFlow(true)
    val actionShowHintsHideResults: StateFlow<Boolean>
        get() = mActionShowHintsHideResults

    override fun initObserversBlock() {
        super.initObserversBlock()

        Log.d("Test", "init")

        viewModelScope.launch(mCoroutineContext.IO) {
            launch {
                mDataInteractor.companiesState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect { mCompanies = it.data!! }
            }

            launch {
                mDataInteractor.searchRequestsState
                    .filter { it is DataNotificator.DataPrepared }
                    .take(1)
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            onSearchesChanged(it.data!!)
                        }
                    }
            }

            launch {
                mDataInteractor.searchRequestsUpdateShared.collect {
                    withContext(mCoroutineContext.Main) {
                        onSearchesChanged(it)
                    }
                }
            }
        }
    }

    fun onSearchTextChanged(editable: Editable?) {
        viewModelScope.launch(mCoroutineContext.IO) {
            editable?.let {
                val searchText = it.toString()

                if(mLastTextSearch != searchText) {
                    mLastTextSearch = searchText

                    if (searchText.isNotEmpty()) {
                        mActionHideCloseIcon.value = false
                        val results = mutableListOf<AdaptiveCompany>()
                        mCompanies?.forEach { company ->
                            if (search(company, searchText)) {
                                results.add(company)
                            }
                        }

                        withContext(mCoroutineContext.Main) {
                            mRecyclerAdapter.invalidate()
                        }

                        if (results.isNotEmpty()) {
                            val resultArray = ArrayList(results)
                            switchSectionsVisibility(false)
                            withContext(mCoroutineContext.Main) {
                                mRecyclerAdapter.setCompaniesWithNotify(resultArray)
                            }
                            if (results.size <= 10) {
                                addToSearched(searchText)
                            }
                        } else switchSectionsVisibility(true)
                    } else {
                        switchSectionsVisibility(true)
                        mActionHideCloseIcon.value = true
                    }
                }


            }
        }
    }

    private fun addToSearched(searchText: String) {
        viewModelScope.launch(mCoroutineContext.IO) {
            mDataInteractor.onNewSearch(searchText)
        }
    }

    private fun onSearchesChanged(data: List<AdaptiveSearchRequest>) {
        mSearchesAdapter.setData(ArrayList(data))
    }

    private fun switchSectionsVisibility(showHintsHideResults: Boolean) {
        mActionShowHintsHideResults.value = showHintsHideResults
    }
}